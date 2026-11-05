package com.imprenta.ordenes_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imprenta.ordenes_service.dto.AjusteCajaDTO;
import com.imprenta.ordenes_service.dto.EgresoDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.CatTipoMovimiento;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.repository.CatCondicionesPagoRepository;
import com.imprenta.ordenes_service.repository.CatTipoMovimientoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;

@ExtendWith(MockitoExtension.class)
class OperacionesServiceTest {

    @Mock
    private CatCondicionesPagoRepository condicionesPagoRepository;
    @Mock
    private CatTipoMovimientoRepository catTipoMovimientoRepository;
    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private OperacionesService operacionesService;

    // ---------- Helpers del test ----------

    private EgresoDTO armarEgresoDTO() {
        EgresoDTO dto = new EgresoDTO();
        dto.setMonto(new BigDecimal("250.00"));
        dto.setConcepto("Compra de tinta");
        dto.setIdTipoMovimiento(2);
        dto.setIdUsuarioRegistro(8);
        return dto;
    }

    private AjusteCajaDTO armarAjusteDTO(BigDecimal monto, String concepto, String tipoAjuste) {
        return new AjusteCajaDTO(monto, concepto, tipoAjuste, 8);
    }

    private Movimientos capturarMovimientoGuardado() {
        ArgumentCaptor<Movimientos> captor = ArgumentCaptor.forClass(Movimientos.class);
        verify(movimientoRepository).save(captor.capture());
        return captor.getValue();
    }

    // ---------- obtenerTodasLasCondiciones ----------

    @Test
    void obtenerTodasLasCondiciones_devuelveLoQueEntregaElRepositorio() {
        List<CatCondicionesPago> condiciones = List.of(new CatCondicionesPago(), new CatCondicionesPago());
        when(condicionesPagoRepository.findAll()).thenReturn(condiciones);

        assertSame(condiciones, operacionesService.obtenerTodasLasCondiciones());
    }

    // ---------- registrarEgreso ----------

    @Test
    void registrarEgreso_conCategoriaValida_guardaMovimientoSinOrden() {
        EgresoDTO dto = armarEgresoDTO();
        Movimientos guardado = new Movimientos();
        when(catTipoMovimientoRepository.findById(2)).thenReturn(Optional.of(new CatTipoMovimiento()));
        when(movimientoRepository.save(any(Movimientos.class))).thenReturn(guardado);

        Movimientos resultado = operacionesService.registrarEgreso(dto);

        assertSame(guardado, resultado);

        Movimientos enviado = capturarMovimientoGuardado();
        assertEquals(0, new BigDecimal("250.00").compareTo(enviado.getMonto()));
        assertEquals("Compra de tinta", enviado.getConcepto());
        assertEquals(2, enviado.getIdTipoMovimiento());
        assertEquals(8, enviado.getIdUsuarioRegistro());
        assertNull(enviado.getIdOrden());
        assertNotNull(enviado.getFechaMovimiento());
        assertTrue(enviado.getFechaMovimiento().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void registrarEgreso_categoriaInexistente_lanzaBadRequestYNoGuarda() {
        EgresoDTO dto = armarEgresoDTO();
        dto.setIdTipoMovimiento(99);
        when(catTipoMovimientoRepository.findById(99)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> operacionesService.registrarEgreso(dto));

        assertTrue(ex.getMessage().contains("99"));
        verifyNoInteractions(movimientoRepository);
    }

    // ---------- registrarAjusteCaja: casos válidos ----------

    @Test
    void registrarAjusteCaja_entrada_guardaMontoNegativoConTipo5() {
        operacionesService.registrarAjusteCaja(armarAjusteDTO(new BigDecimal("100.00"), "Fondo inicial", "ENTRADA"));

        Movimientos guardado = capturarMovimientoGuardado();
        assertEquals(0, new BigDecimal("-100.00").compareTo(guardado.getMonto()));
        assertEquals(5, guardado.getIdTipoMovimiento());
        assertEquals("Fondo inicial", guardado.getConcepto());
        assertEquals(8, guardado.getIdUsuarioRegistro());
        assertNotNull(guardado.getFechaMovimiento());
    }

    @Test
    void registrarAjusteCaja_salida_guardaMontoPositivoConTipo5() {
        operacionesService.registrarAjusteCaja(armarAjusteDTO(new BigDecimal("75.50"), "Retiro", "SALIDA"));

        Movimientos guardado = capturarMovimientoGuardado();
        assertEquals(0, new BigDecimal("75.50").compareTo(guardado.getMonto()));
        assertEquals(5, guardado.getIdTipoMovimiento());
    }

    @Test
    void registrarAjusteCaja_tipoEnMinusculas_seAceptaSinImportarMayusculas() {
        operacionesService.registrarAjusteCaja(armarAjusteDTO(new BigDecimal("40.00"), "Ajuste", "entrada"));

        Movimientos guardado = capturarMovimientoGuardado();
        assertEquals(0, new BigDecimal("-40.00").compareTo(guardado.getMonto()));
    }

    // ---------- registrarAjusteCaja: validaciones ----------

    @Test
    void registrarAjusteCaja_montoNull_lanzaIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> operacionesService.registrarAjusteCaja(armarAjusteDTO(null, "Concepto", "ENTRADA")));

        assertTrue(ex.getMessage().contains("mayor a cero"));
        verifyNoInteractions(movimientoRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "0.00", "-1", "-250.75" })
    void registrarAjusteCaja_montoCeroONegativo_lanzaIllegalArgument(String monto) {
        assertThrows(IllegalArgumentException.class,
                () -> operacionesService
                        .registrarAjusteCaja(armarAjusteDTO(new BigDecimal(monto), "Concepto", "ENTRADA")));

        verifyNoInteractions(movimientoRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void registrarAjusteCaja_conceptoNullVacioOEspacios_lanzaIllegalArgument(String concepto) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> operacionesService
                        .registrarAjusteCaja(armarAjusteDTO(new BigDecimal("10.00"), concepto, "ENTRADA")));

        assertTrue(ex.getMessage().contains("concepto"));
        verifyNoInteractions(movimientoRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "OTRO", "ENTRADAS" })
    void registrarAjusteCaja_tipoInvalido_lanzaIllegalArgument(String tipo) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> operacionesService
                        .registrarAjusteCaja(armarAjusteDTO(new BigDecimal("10.00"), "Concepto", tipo)));

        assertTrue(ex.getMessage().contains("ENTRADA o SALIDA"));
        verifyNoInteractions(movimientoRepository);
    }
}