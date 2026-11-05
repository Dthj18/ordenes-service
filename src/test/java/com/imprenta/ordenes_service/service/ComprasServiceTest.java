package com.imprenta.ordenes_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
import com.imprenta.ordenes_service.dto.ProductoSolicitudDTO;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.DetalleSolicitud;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.SolicitudInsumoRepository;

@ExtendWith(MockitoExtension.class)
class ComprasServiceTest {

    @Mock
    private SolicitudInsumoRepository solicitudInsumoRepository;
    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private SecurityHelper securityHelper;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ComprasService comprasService;

    // ---------- Helpers del test ----------

    private ProductoSolicitudDTO producto(Integer idProducto, String cantidad) {
        ProductoSolicitudDTO p = new ProductoSolicitudDTO();
        p.setIdProducto(idProducto);
        p.setCantidad(cantidad);
        return p;
    }

    private CrearSolicitudDTO armarSolicitudDTO(Integer idOrden, List<ProductoSolicitudDTO> productos) {
        CrearSolicitudDTO dto = new CrearSolicitudDTO();
        dto.setIdUsuario(3);
        dto.setIdOrden(idOrden);
        dto.setDescripcionGeneral("Papel couché y tinta");
        dto.setProductos(productos);
        return dto;
    }

    /** save() devuelve la misma entidad que recibe, para poder inspeccionarla. */
    private void stubSaveSolicitudDevuelveArgumento() {
        when(solicitudInsumoRepository.save(any(SolicitudInsumo.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private SolicitudInsumo solicitudExistente(String descripcion, int numDetalles, Integer idOrden) {
        SolicitudInsumo s = new SolicitudInsumo();
        s.setIdSolicitud(7);
        s.setDescripcionSolicitud(descripcion);
        s.setIdOrden(idOrden);
        for (int i = 0; i < numDetalles; i++) {
            s.agregarDetalle(new DetalleSolicitud());
        }
        return s;
    }

    // ---------- crearSolicitud ----------

    @Test
    void crearSolicitud_conProductosYOrden_guardaPendienteYNotifica() {
        CrearSolicitudDTO dto = armarSolicitudDTO(5,
                List.of(producto(10, "4"), producto(11, "2")));
        stubSaveSolicitudDevuelveArgumento();

        SolicitudInsumo resultado = comprasService.crearSolicitud(dto);

        assertEquals(3, resultado.getIdUsuarioSolicitante());
        assertEquals(5, resultado.getIdOrden());
        assertEquals(1, resultado.getIdEstatus()); // ESTATUS_PENDIENTE
        assertEquals("Papel couché y tinta", resultado.getDescripcionSolicitud());
        assertNotNull(resultado.getFechaCreacion());
        assertEquals(2, resultado.getDetalles().size());
        assertEquals(4, resultado.getDetalles().get(0).getCantidad());
        assertEquals(2, resultado.getDetalles().get(1).getCantidad());

        verify(notificationService).notificarARoles(
                "3",
                List.of("CONTADORA", "DUEÑO"),
                "SOLICITUD_INSUMOS",
                "Nueva solicitud de insumos",
                "Se ha generado una nueva solicitud de insumos para la orden #5.");
    }

    @Test
    void crearSolicitud_cantidadNoNumerica_usaCantidadUno() {
        CrearSolicitudDTO dto = armarSolicitudDTO(5, List.of(producto(10, "abc")));
        stubSaveSolicitudDevuelveArgumento();

        SolicitudInsumo resultado = comprasService.crearSolicitud(dto);

        assertEquals(1, resultado.getDetalles().size());
        assertEquals(1, resultado.getDetalles().get(0).getCantidad());
    }

    @Test
    void crearSolicitud_sinOrden_mensajeNoIncluyeNumeroDeOrden() {
        CrearSolicitudDTO dto = armarSolicitudDTO(null, List.of(producto(10, "1")));
        stubSaveSolicitudDevuelveArgumento();

        comprasService.crearSolicitud(dto);

        verify(notificationService).notificarARoles(
                "3",
                List.of("CONTADORA", "DUEÑO"),
                "SOLICITUD_INSUMOS",
                "Nueva solicitud de insumos",
                "Se ha generado una nueva solicitud de insumos.");
    }

    @Test
    void crearSolicitud_productosNull_guardaSinDetalles() {
        CrearSolicitudDTO dto = armarSolicitudDTO(5, null);
        stubSaveSolicitudDevuelveArgumento();

        SolicitudInsumo resultado = comprasService.crearSolicitud(dto);

        assertTrue(resultado.getDetalles() == null || resultado.getDetalles().isEmpty());
        verify(solicitudInsumoRepository).save(any(SolicitudInsumo.class));
    }

    // ---------- registrarCompra ----------

    @Test
    void registrarCompra_valida_marcaCompradaYRegistraEgresoNegativo() {
        SolicitudInsumo solicitud = solicitudExistente("Papel", 2, 5);
        when(solicitudInsumoRepository.findById(7)).thenReturn(Optional.of(solicitud));
        when(movimientoRepository.save(any(Movimientos.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Movimientos resultado = comprasService.registrarCompra(7, new BigDecimal("1500.50"), 9);

        // Permisos y cambio de estatus de la solicitud
        verify(securityHelper).validarPermiso(9, SecurityHelper.ROL_ADMIN, SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_CONTADORA);
        assertEquals(2, solicitud.getIdEstatus()); // ESTATUS_COMPRADA
        verify(solicitudInsumoRepository).save(solicitud);

        // Movimiento generado
        assertEquals(0, new BigDecimal("-1500.50").compareTo(resultado.getMonto()));
        assertEquals(2, resultado.getIdTipoMovimiento()); // TIPO_EGRESO
        assertEquals("Compra Sol #7: Papel (2 items)", resultado.getConcepto());
        assertEquals(9, resultado.getIdUsuarioRegistro());
        assertEquals(5, resultado.getIdOrden());
        assertNotNull(resultado.getFechaMovimiento());
    }

    @Test
    void registrarCompra_costoYaNegativo_sigueSiendoNegativo() {
        SolicitudInsumo solicitud = solicitudExistente("Papel", 1, null);
        when(solicitudInsumoRepository.findById(7)).thenReturn(Optional.of(solicitud));
        when(movimientoRepository.save(any(Movimientos.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Movimientos resultado = comprasService.registrarCompra(7, new BigDecimal("-200.00"), 9);

        assertEquals(0, new BigDecimal("-200.00").compareTo(resultado.getMonto()));
        assertNull(resultado.getIdOrden());
    }

    @Test
    void registrarCompra_sinDescripcion_usaConceptoGenerico() {
        SolicitudInsumo solicitud = solicitudExistente(null, 0, null);
        when(solicitudInsumoRepository.findById(7)).thenReturn(Optional.of(solicitud));
        when(movimientoRepository.save(any(Movimientos.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Movimientos resultado = comprasService.registrarCompra(7, new BigDecimal("100"), 9);

        assertEquals("Compra Sol #7: Compra de insumos varios (0 items)", resultado.getConcepto());
    }

    @Test
    void registrarCompra_conceptoMuyLargo_seTruncaA250Caracteres() {
        String descripcionLarga = "x".repeat(400);
        SolicitudInsumo solicitud = solicitudExistente(descripcionLarga, 1, null);
        when(solicitudInsumoRepository.findById(7)).thenReturn(Optional.of(solicitud));
        when(movimientoRepository.save(any(Movimientos.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Movimientos resultado = comprasService.registrarCompra(7, new BigDecimal("100"), 9);

        assertEquals(250, resultado.getConcepto().length());
        assertTrue(resultado.getConcepto().endsWith("..."));
    }

    @Test
    void registrarCompra_guardaMovimientoConElCapturorEsperado() {
        SolicitudInsumo solicitud = solicitudExistente("Tinta", 1, 5);
        Movimientos guardado = new Movimientos();
        when(solicitudInsumoRepository.findById(7)).thenReturn(Optional.of(solicitud));
        when(movimientoRepository.save(any(Movimientos.class))).thenReturn(guardado);

        Movimientos resultado = comprasService.registrarCompra(7, new BigDecimal("300"), 9);

        assertSame(guardado, resultado);
        ArgumentCaptor<Movimientos> captor = ArgumentCaptor.forClass(Movimientos.class);
        verify(movimientoRepository).save(captor.capture());
        assertEquals(0, new BigDecimal("-300").compareTo(captor.getValue().getMonto()));
    }

    @Test
    void registrarCompra_solicitudInexistente_lanzaResourceNotFound() {
        when(solicitudInsumoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> comprasService.registrarCompra(99, new BigDecimal("100"), 9));

        verify(solicitudInsumoRepository, never()).save(any());
        verifyNoInteractions(movimientoRepository);
    }

    @Test
    void registrarCompra_sinPermiso_noTocaRepositorios() {
        doThrow(new RuntimeException("Sin permiso"))
                .when(securityHelper)
                .validarPermiso(9, SecurityHelper.ROL_ADMIN, SecurityHelper.ROL_DUENO,
                        SecurityHelper.ROL_CONTADORA);

        assertThrows(RuntimeException.class,
                () -> comprasService.registrarCompra(7, new BigDecimal("100"), 9));

        verifyNoInteractions(solicitudInsumoRepository, movimientoRepository);
    }

    // ---------- obtenerPendientes ----------

    @Test
    void obtenerPendientes_devuelveSolicitudesConEstatusPendiente() {
        List<SolicitudInsumo> pendientes = new ArrayList<>();
        pendientes.add(solicitudExistente("Papel", 2, null));
        pendientes.add(solicitudExistente("Tinta", 1, null));
        when(solicitudInsumoRepository.findByIdEstatus(1)).thenReturn(pendientes);

        List<SolicitudInsumo> resultado = comprasService.obtenerPendientes();

        assertEquals(2, resultado.size());
        verify(solicitudInsumoRepository).findByIdEstatus(1);
    }

    @Test
    void obtenerPendientes_sinPendientes_devuelveListaVacia() {
        when(solicitudInsumoRepository.findByIdEstatus(1)).thenReturn(new ArrayList<>());

        assertTrue(comprasService.obtenerPendientes().isEmpty());
    }
}
