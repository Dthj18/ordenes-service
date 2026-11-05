package com.imprenta.ordenes_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imprenta.ordenes_service.client.PersonasClient;
import com.imprenta.ordenes_service.dto.ClienteDTO;
import com.imprenta.ordenes_service.dto.CrearOrdenDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.CotizacionHelper;
import com.imprenta.ordenes_service.helpers.FileStorageHelper;
import com.imprenta.ordenes_service.helpers.MovimientosHelper;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.helpers.TrazabilidadHelper;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Trazabilidad;
import com.imprenta.ordenes_service.repository.CatEstatusRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.states.OrdenStateFactory;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;
    @Mock
    private CotizacionHelper cotizacionHelper;
    @Mock
    private TrazabilidadHelper trazabilidadHelper;
    @Mock
    private MovimientosHelper movimientosHelper;
    @Mock
    private OrdenStateFactory stateFactory;
    @Mock
    private FileStorageHelper fileStorageHelper;
    @Mock
    private SecurityHelper securityHelper;
    @Mock
    private CatEstatusRepository catEstatusRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PersonasClient personasClient;

    @InjectMocks
    private OrdenService ordenService;

    private ClienteDTO clienteConCredito;

    @BeforeEach
    void setUp() {
        clienteConCredito = new ClienteDTO();
        clienteConCredito.setCreditoHabilitado(true);
        clienteConCredito.setEstatusCredito("ACTIVO");
        clienteConCredito.setDiasCredito(15);
        clienteConCredito.setLimiteCredito(new BigDecimal("15000.00"));
    }

    // ---------- Helpers del test ----------

    private CrearOrdenDTO armarRequest(Integer idCondicionPago, String monto) {
        Orden orden = new Orden();
        orden.setIdCliente(322);
        orden.setIdCondicionPago(idCondicionPago);
        orden.setMontoTotal(new BigDecimal(monto));

        CrearOrdenDTO dto = new CrearOrdenDTO();
        dto.setOrden(orden);
        dto.setDetalles(new ArrayList<>());
        dto.setIdUsuarioAccion(2);
        return dto;
    }

    // ---------- crearCotizacion ----------

    @Test
    void crearCotizacion_conCreditoSuficiente_creaCotizacionYRegistraTrazabilidad() {
        CrearOrdenDTO request = armarRequest(5, "6850.00");

        Orden ordenGuardada = new Orden();
        ordenGuardada.setIdOrden(101);

        when(personasClient.obtenerClientePorId(322)).thenReturn(clienteConCredito);
        when(ordenRepository.calcularSaldoDeudor(322)).thenReturn(new BigDecimal("5000.00"));
        when(cotizacionHelper.generarCotizacion(request.getOrden(), request.getDetalles()))
                .thenReturn(ordenGuardada);

        Orden resultado = ordenService.crearCotizacion(request);

        assertSame(ordenGuardada, resultado);
        LocalDateTime vencimiento = request.getOrden().getFechaVencimiento();
        assertNotNull(vencimiento);
        assertTrue(vencimiento.isAfter(LocalDateTime.now().plusDays(14)));
        assertTrue(vencimiento.isBefore(LocalDateTime.now().plusDays(16)));

        verify(trazabilidadHelper).registrarCambio(101, "COT_INICIADA", 2);
    }

    @Test
    void crearCotizacion_sinCreditoHabilitado_lanzaBadRequest() {
        clienteConCredito.setCreditoHabilitado(false);
        CrearOrdenDTO request = armarRequest(5, "1000.00");
        when(personasClient.obtenerClientePorId(322)).thenReturn(clienteConCredito);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenService.crearCotizacion(request));

        assertTrue(ex.getMessage().contains("no tiene crédito habilitado"));
        verifyNoInteractions(cotizacionHelper, trazabilidadHelper);
    }

    @Test
    void crearCotizacion_creditoHabilitadoNull_lanzaBadRequest() {
        clienteConCredito.setCreditoHabilitado(null);
        CrearOrdenDTO request = armarRequest(5, "1000.00");
        when(personasClient.obtenerClientePorId(322)).thenReturn(clienteConCredito);

        assertThrows(BadRequestException.class, () -> ordenService.crearCotizacion(request));
        verifyNoInteractions(cotizacionHelper, trazabilidadHelper);
    }

    @Test
    void crearCotizacion_estatusCreditoAtrasado_lanzaBadRequest() {
        clienteConCredito.setEstatusCredito("ATRASADO");
        CrearOrdenDTO request = armarRequest(5, "1000.00");
        when(personasClient.obtenerClientePorId(322)).thenReturn(clienteConCredito);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenService.crearCotizacion(request));

        assertTrue(ex.getMessage().contains("ATRASADO"));
        verifyNoInteractions(cotizacionHelper, trazabilidadHelper);
    }

    @Test
    void crearCotizacion_montoSuperaCreditoDisponible_lanzaBadRequest() {
        CrearOrdenDTO request = armarRequest(5, "6850.00");
        when(personasClient.obtenerClientePorId(322)).thenReturn(clienteConCredito);
        when(ordenRepository.calcularSaldoDeudor(322)).thenReturn(new BigDecimal("14000.00"));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ordenService.crearCotizacion(request));

        assertTrue(ex.getMessage().contains("supera el límite de crédito"));
        verifyNoInteractions(cotizacionHelper, trazabilidadHelper);
    }

    @Test
    void crearCotizacion_pagoDeContado_noConsultaCreditoYVenceHoy() {
        CrearOrdenDTO request = armarRequest(1, "6850.00");

        Orden ordenGuardada = new Orden();
        ordenGuardada.setIdOrden(102);
        when(cotizacionHelper.generarCotizacion(request.getOrden(), request.getDetalles()))
                .thenReturn(ordenGuardada);

        Orden resultado = ordenService.crearCotizacion(request);

        assertEquals(102, resultado.getIdOrden());
        verifyNoInteractions(personasClient);
        verify(ordenRepository, never()).calcularSaldoDeudor(any());
        verify(trazabilidadHelper).registrarCambio(102, "COT_INICIADA", 2);
    }

    // ---------- findById ----------

    @Test
    void findById_existente_devuelveOrden() {
        Orden orden = new Orden();
        orden.setIdOrden(10);
        when(ordenRepository.findById(10)).thenReturn(Optional.of(orden));

        assertSame(orden, ordenService.findById(10));
    }

    @Test
    void findById_inexistente_lanzaResourceNotFound() {
        when(ordenRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> ordenService.findById(99));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- cancelarOrden ----------

    @Test
    void cancelarOrden_estatusValido_cancelaYNotifica() {
        Orden orden = new Orden();
        orden.setIdOrden(10);
        orden.setIdEstatusActual(3);
        Trazabilidad trazabilidad = new Trazabilidad();

        when(ordenRepository.findById(10)).thenReturn(Optional.of(orden));
        when(trazabilidadHelper.registrarCambioPorId(10, 11, 7)).thenReturn(trazabilidad);

        Trazabilidad resultado = ordenService.cancelarOrden(10, 7, 4);

        assertSame(trazabilidad, resultado);
        assertEquals(4, orden.getIdRazonCancelacion());
        verify(securityHelper).validarPermiso(7, SecurityHelper.ROL_MOSTRADOR);
        verify(notificationService).notificarARoles(
                eq("7"), anyList(), eq("ORDEN_CANCELADA"), anyString(), anyString());
    }

    @Test
    void cancelarOrden_yaCancelada_lanzaBadRequest() {
        Orden orden = new Orden();
        orden.setIdEstatusActual(11);
        when(ordenRepository.findById(10)).thenReturn(Optional.of(orden));

        assertThrows(BadRequestException.class, () -> ordenService.cancelarOrden(10, 7, 4));

        verifyNoInteractions(trazabilidadHelper, notificationService);
    }

    @Test
    void cancelarOrden_yaFinalizada_lanzaBadRequest() {
        Orden orden = new Orden();
        orden.setIdEstatusActual(12);
        when(ordenRepository.findById(10)).thenReturn(Optional.of(orden));

        assertThrows(BadRequestException.class, () -> ordenService.cancelarOrden(10, 7, 4));

        verifyNoInteractions(trazabilidadHelper, notificationService);
    }
}