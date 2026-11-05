package com.imprenta.ordenes_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imprenta.ordenes_service.dto.EgresoDTO;
import com.imprenta.ordenes_service.dto.graficas.BarraEgresosDTO;
import com.imprenta.ordenes_service.dto.graficas.BarraIngresosDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardEgresosDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardFinanzasDTO;
import com.imprenta.ordenes_service.dto.graficas.DashboardIngresosDTO;
import com.imprenta.ordenes_service.dto.graficas.GraficaFinanzasDTO;
import com.imprenta.ordenes_service.dto.reportes.CotizacionesGraficaDTO;
import com.imprenta.ordenes_service.dto.reportes.DashboardResumenDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenesGraficaDTO;
import com.imprenta.ordenes_service.dto.tablas.FlujoCajaDTO;
import com.imprenta.ordenes_service.dto.tablas.MovimientoTablaDTO;
import com.imprenta.ordenes_service.model.CatTipoMovimiento;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.CatTipoMovimientoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@ExtendWith(MockitoExtension.class)
class GraficasServiceTest {

    private static final List<Integer> IDS_EGRESOS = List.of(2, 3, 4, 5);
    private static final String COLOR_HOY = "#5C7CFA";
    private static final String COLOR_OTRO = "#E0E7FF";

    @Mock
    private OrdenRepository ordenRepository;
    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private CatTipoMovimientoRepository catTipoMovimientoRepository;

    @InjectMocks
    private GraficasService graficasService;

    private Long resCotizaciones(DashboardResumenDTO d) {
        return d.getCotizacionesMes();
    }

    private Long resOrdenesActivas(DashboardResumenDTO d) {
        return d.getOrdenesActivas();
    }

    private Long resPagosPendientes(DashboardResumenDTO d) {
        return d.getPagosPendientes();
    }

    private Double resPagosTotal(DashboardResumenDTO d) {
        return d.getPagoTotal();
    }

    // DashboardIngresosDTO / BarraIngresosDTO
    private BigDecimal ingTotal(DashboardIngresosDTO d) {
        return d.getMontoPagado();
    }

    private List<BarraIngresosDTO> ingBarras(DashboardIngresosDTO d) {
        return d.getDatosGrafica();
    }

    private double barraValor(BarraIngresosDTO b) {
        return b.getValue();
    }

    private String barraLabel(BarraIngresosDTO b) {
        return b.getLabel();
    }

    private String barraColor(BarraIngresosDTO b) {
        return b.getFrontColor();
    }

    // EgresoDTO
    private String egresoConcepto(EgresoDTO e) {
        return e.getConcepto();
    }

    private String egresoTipo(EgresoDTO e) {
        return e.getTipoEgreso();
    }

    // DashboardEgresosDTO / BarraEgresosDTO
    private BigDecimal egrTotal(DashboardEgresosDTO d) {
        return d.getMontoEgresado();
    }

    private List<BarraEgresosDTO> egrBarras(DashboardEgresosDTO d) {
        return d.getDatosGrafica();
    }

    private BigDecimal egrBarraValor(BarraEgresosDTO b) {
        return b.getTotal();
    }

    private String egrBarraLabel(BarraEgresosDTO b) {
        return b.getLabel();
    }

    // MovimientoTablaDTO
    private String tablaConcepto(MovimientoTablaDTO m) {
        return m.getConcepto();
    }

    private String tablaTipo(MovimientoTablaDTO m) {
        return m.getTipoMovimiento();
    }

    private BigDecimal tablaMonto(MovimientoTablaDTO m) {
        return m.getMonto();
    }

    // FlujoCajaDTO
    private BigDecimal flujoIngresos(FlujoCajaDTO f) {
        return f.getTotalIngresos();
    }

    private BigDecimal flujoEntrada(FlujoCajaDTO f) {
        return f.getEntradaCaja();
    }

    private BigDecimal flujoEgresos(FlujoCajaDTO f) {
        return f.getTotalEgresos();
    }

    private BigDecimal flujoBalance(FlujoCajaDTO f) {
        return f.getBalance();
    }

    // DashboardFinanzasDTO / GraficaFinanzasDTO
    private BigDecimal finBalance(DashboardFinanzasDTO d) {
        return d.getBalanceTotal();
    }

    private List<GraficaFinanzasDTO> finGrafica(DashboardFinanzasDTO d) {
        return d.getDatosGrafica();
    }

    private String gLabel(GraficaFinanzasDTO g) {
        return g.getLabel();
    }

    private BigDecimal gIngresos(GraficaFinanzasDTO g) {
        return g.getIngresos();
    }

    private BigDecimal gEgresos(GraficaFinanzasDTO g) {
        return g.getEgresos();
    }

    // =====================================================================
    // Helpers del test
    // =====================================================================

    private Orden orden(LocalDateTime fecha, String montoPagado) {
        Orden o = new Orden();
        o.setFechaCreacion(fecha);
        o.setMontoPagado(montoPagado == null ? null : new BigDecimal(montoPagado));
        return o;
    }

    private Orden ordenConMontoTotal(LocalDateTime fecha, String montoTotal) {
        Orden o = new Orden();
        o.setFechaCreacion(fecha);
        o.setMontoTotal(montoTotal == null ? null : new BigDecimal(montoTotal));
        return o;
    }

    private Movimientos mov(Integer tipo, String monto, LocalDateTime fecha) {
        Movimientos m = new Movimientos();
        m.setIdTipoMovimiento(tipo);
        m.setMonto(monto == null ? null : new BigDecimal(monto));
        m.setFechaMovimiento(fecha);
        return m;
    }

    private void assertMonto(String esperado, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(esperado).compareTo(actual),
                "Esperado " + esperado + " pero fue " + actual);
    }

    private void stubIngresosFinanzas(List<Orden> ordenes) {
        when(ordenRepository.encontrarOrdenesParaFinanzas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(ordenes);
    }

    private void stubOrdenesGrafica(List<Orden> ordenes) {
        when(ordenRepository.findOrdenesPorRangoParaGrafica(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(ordenes);
    }

    private void stubEgresos(List<Movimientos> movimientos) {
        when(movimientoRepository.findEgresosPorRango(eq(IDS_EGRESOS), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(movimientos);
    }

    // =====================================================================
    // obtenerDatosGraficasMovil
    // =====================================================================

    @Test
    void obtenerDatosGraficasMovil_devuelvePastelYRadar() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 1, 31, 23, 59);

        Map<String, Object> resultado = graficasService.obtenerDatosGraficasMovil(1, inicio, fin);

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("datosPastel"));
        assertTrue(resultado.containsKey("datosRadar"));
        verify(ordenRepository).obtenerResumenEstatus(inicio, fin);
        verify(ordenRepository).obtenerRazonesRechazo(inicio, fin);
    }

    // =====================================================================
    // obtenerResumen
    // =====================================================================

    @Test
    void obtenerResumen_armaDtoConLosConteosDelRepositorio() {
        when(ordenRepository.contarCotizacionesMes(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(5L);
        when(ordenRepository.contarOrdenesActivas()).thenReturn(3L);
        when(ordenRepository.contarPagosPendientes()).thenReturn(2L);
        when(ordenRepository.sumarDineroPendiente()).thenReturn(1500.0);

        DashboardResumenDTO resultado = graficasService.obtenerResumen();

        assertEquals(5L, resCotizaciones(resultado));
        assertEquals(3L, resOrdenesActivas(resultado));
        assertEquals(2L, resPagosPendientes(resultado));
        assertEquals(1500.0, resPagosTotal(resultado));
    }

    @Test
    void obtenerResumen_consultaElMesActualCompleto() {
        graficasService.obtenerResumen();

        ArgumentCaptor<LocalDateTime> inicio = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fin = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(ordenRepository).contarCotizacionesMes(inicio.capture(), fin.capture());

        LocalDateTime hoy = LocalDateTime.now();
        assertEquals(1, inicio.getValue().getDayOfMonth());
        assertEquals(LocalTime.MIN, inicio.getValue().toLocalTime());
        assertEquals(hoy.toLocalDate().lengthOfMonth(), fin.getValue().getDayOfMonth());
        assertEquals(hoy.getMonth(), fin.getValue().getMonth());
    }

    // =====================================================================
    // obtenerDashboardIngresos
    // =====================================================================

    private LocalDateTime[] capturarRangoIngresos(String filtro) {
        graficasService.obtenerDashboardIngresos(filtro);
        ArgumentCaptor<LocalDateTime> inicio = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fin = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(ordenRepository).encontrarOrdenesParaFinanzas(inicio.capture(), fin.capture());
        return new LocalDateTime[] { inicio.getValue(), fin.getValue() };
    }

    @Test
    void dashboardIngresos_filtroHoy_rangoEsElDiaActual() {
        LocalDateTime[] rango = capturarRangoIngresos("hoy");

        assertEquals(LocalDateTime.now().toLocalDate(), rango[0].toLocalDate());
        assertEquals(LocalTime.of(0, 0), rango[0].toLocalTime());
        assertEquals(LocalDateTime.now().toLocalDate(), rango[1].toLocalDate());
        assertEquals(23, rango[1].getHour());
        assertEquals(59, rango[1].getMinute());
    }

    @Test
    void dashboardIngresos_filtroSemana_rangoVaDeLunesADomingo() {
        LocalDateTime[] rango = capturarRangoIngresos("semana");

        assertEquals(DayOfWeek.MONDAY, rango[0].getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, rango[1].getDayOfWeek());
    }

    @Test
    void dashboardIngresos_filtroAnio_rangoVaDel1DeEneroAl31DeDiciembre() {
        LocalDateTime[] rango = capturarRangoIngresos("anio");

        assertEquals(1, rango[0].getDayOfYear());
        assertEquals(12, rango[1].getMonthValue());
        assertEquals(31, rango[1].getDayOfMonth());
    }

    @Test
    void dashboardIngresos_filtroHoy_sumaMontosIgnorandoNullYDevuelveUnaBarra() {
        LocalDateTime ahora = LocalDateTime.now();
        stubIngresosFinanzas(List.of(orden(ahora, "100.00"), orden(ahora, null), orden(ahora, "50.50")));

        DashboardIngresosDTO resultado = graficasService.obtenerDashboardIngresos("hoy");

        assertMonto("150.50", ingTotal(resultado));
        assertEquals(1, ingBarras(resultado).size());
        assertEquals("Hoy", barraLabel(ingBarras(resultado).get(0)));
        assertEquals(150.50, barraValor(ingBarras(resultado).get(0)), 0.001);
    }

    @Test
    void dashboardIngresos_filtroSemana_agrupaPorDiaYResaltaElDiaDeHoy() {
        LocalDateTime ahora = LocalDateTime.now();
        stubIngresosFinanzas(List.of(
                orden(ahora.with(DayOfWeek.MONDAY), "200.00"),
                orden(ahora.with(DayOfWeek.WEDNESDAY), "300.00"),
                orden(ahora.with(DayOfWeek.WEDNESDAY), null)));

        DashboardIngresosDTO resultado = graficasService.obtenerDashboardIngresos("Semana");
        List<BarraIngresosDTO> barras = ingBarras(resultado);

        assertMonto("500.00", ingTotal(resultado));
        assertEquals(7, barras.size());
        assertEquals("Lun", barraLabel(barras.get(0)));
        assertEquals(200.0, barraValor(barras.get(0)), 0.001);
        assertEquals(300.0, barraValor(barras.get(2)), 0.001);
        assertEquals(0.0, barraValor(barras.get(6)), 0.001);

        int indiceHoy = ahora.getDayOfWeek().getValue() - 1;
        for (int i = 0; i < 7; i++) {
            assertEquals(i == indiceHoy ? COLOR_HOY : COLOR_OTRO, barraColor(barras.get(i)));
        }
    }

    @Test
    void dashboardIngresos_filtroMes_agrupaEnCuatroSemanas() {
        LocalDateTime ahora = LocalDateTime.now();
        stubIngresosFinanzas(List.of(
                orden(ahora.withDayOfMonth(1), "100.00"), // Sem 1 (días 1-7)
                orden(ahora.withDayOfMonth(8), "250.00"))); // Sem 2 (días 8-14)

        DashboardIngresosDTO resultado = graficasService.obtenerDashboardIngresos("mes");
        List<BarraIngresosDTO> barras = ingBarras(resultado);

        assertMonto("350.00", ingTotal(resultado));
        assertEquals(4, barras.size());
        assertEquals("Sem 1", barraLabel(barras.get(0)));
        assertEquals(100.0, barraValor(barras.get(0)), 0.001);
        assertEquals(250.0, barraValor(barras.get(1)), 0.001);
        assertEquals(0.0, barraValor(barras.get(2)), 0.001);
    }

    @Test
    void dashboardIngresos_filtroAnio_agrupaPorMes() {
        LocalDateTime ahora = LocalDateTime.now();
        stubIngresosFinanzas(List.of(orden(ahora.withDayOfMonth(1).withMonth(3), "700.00")));

        DashboardIngresosDTO resultado = graficasService.obtenerDashboardIngresos("año");
        List<BarraIngresosDTO> barras = ingBarras(resultado);

        assertEquals(12, barras.size());
        assertEquals("Mar", barraLabel(barras.get(2)));
        assertEquals(700.0, barraValor(barras.get(2)), 0.001);
        assertEquals(COLOR_HOY, barraColor(barras.get(ahora.getMonthValue() - 1)));
    }

    @Test
    void dashboardIngresos_filtroDesconocido_usaRangoDeMesPeroUnaSolaBarraHoy() {
        // Comportamiento actual: el rango cae en "mes" pero las barras en el else.
        LocalDateTime[] rango = capturarRangoIngresos("xyz");
        assertEquals(1, rango[0].getDayOfMonth());
    }

    @Test
    void dashboardIngresos_filtroDesconocido_devuelveBarraHoy() {
        stubIngresosFinanzas(List.of(orden(LocalDateTime.now(), "80.00")));

        DashboardIngresosDTO resultado = graficasService.obtenerDashboardIngresos("xyz");

        assertEquals(1, ingBarras(resultado).size());
        assertEquals("Hoy", barraLabel(ingBarras(resultado).get(0)));
    }

    // =====================================================================
    // obtenerEgresos
    // =====================================================================

    @Test
    void obtenerEgresos_mapeaTipoYUsaDesconocidoCuandoNoExisteEnCatalogo() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 10, 12, 0);
        Movimientos conocido = mov(2, "100.00", fecha);
        conocido.setConcepto("Compra papel");
        Movimientos desconocido = mov(99, "40.00", fecha);
        desconocido.setConcepto("Otro gasto");

        CatTipoMovimiento tipo = new CatTipoMovimiento();
        tipo.setIdTipo(2);
        tipo.setDescripcion("Compra de insumos");

        when(movimientoRepository.findByIdTipoMovimientoInOrderByFechaMovimientoDesc(IDS_EGRESOS))
                .thenReturn(List.of(conocido, desconocido));
        when(catTipoMovimientoRepository.findAll()).thenReturn(List.of(tipo));

        List<EgresoDTO> resultado = graficasService.obtenerEgresos();

        assertEquals(2, resultado.size());
        assertEquals("Compra papel", egresoConcepto(resultado.get(0)));
        assertEquals("Compra de insumos", egresoTipo(resultado.get(0)));
        assertEquals("Desconocido", egresoTipo(resultado.get(1)));
    }

    @Test
    void obtenerEgresos_sinMovimientos_devuelveListaVacia() {
        when(movimientoRepository.findByIdTipoMovimientoInOrderByFechaMovimientoDesc(IDS_EGRESOS))
                .thenReturn(List.of());
        when(catTipoMovimientoRepository.findAll()).thenReturn(List.of());

        assertTrue(graficasService.obtenerEgresos().isEmpty());
    }

    // =====================================================================
    // obtenerGraficaCotizaciones
    // =====================================================================

    @Test
    void graficaCotizaciones_filtroHoy_tiene24PuntosYAcumulaPorHora() {
        LocalDateTime a = LocalDateTime.now().withHour(10).withMinute(5);
        when(ordenRepository.findCotizacionesPorRangoParaGrafica(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        ordenConMontoTotal(a, "100.00"),
                        ordenConMontoTotal(a.withMinute(40), "50.00"),
                        ordenConMontoTotal(a.withMinute(50), null)));

        List<CotizacionesGraficaDTO> resultado = graficasService.obtenerGraficaCotizaciones("hoy");

        assertEquals(24, resultado.size());
        assertEquals(3L, resultado.get(10).getTotalCotizaciones());
        assertMonto("150.00", resultado.get(10).getMontoTotal());
        assertEquals(0L, resultado.get(9).getTotalCotizaciones());
    }

    @Test
    void graficaCotizaciones_filtroAnio_tiene12PuntosYAcumulaPorMes() {
        LocalDateTime marzo = LocalDateTime.now().withDayOfMonth(1).withMonth(3);
        when(ordenRepository.findCotizacionesPorRangoParaGrafica(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ordenConMontoTotal(marzo, "900.00")));

        List<CotizacionesGraficaDTO> resultado = graficasService.obtenerGraficaCotizaciones("año");

        assertEquals(12, resultado.size());
        assertEquals(1L, resultado.get(2).getTotalCotizaciones());
        assertMonto("900.00", resultado.get(2).getMontoTotal());
    }

    @Test
    void graficaCotizaciones_filtroSemana_tiene7PuntosYLunesEsElPrimero() {
        LocalDateTime lunes = LocalDateTime.now().with(DayOfWeek.MONDAY);
        when(ordenRepository.findCotizacionesPorRangoParaGrafica(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ordenConMontoTotal(lunes, "200.00")));

        List<CotizacionesGraficaDTO> resultado = graficasService.obtenerGraficaCotizaciones("semana");

        assertEquals(7, resultado.size());
        assertEquals(1L, resultado.get(0).getTotalCotizaciones());
    }

    @Test
    void graficaCotizaciones_filtroMes_tieneUnPuntoPorDiaDelMes() {
        LocalDateTime dia1 = LocalDateTime.now().withDayOfMonth(1);
        when(ordenRepository.findCotizacionesPorRangoParaGrafica(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ordenConMontoTotal(dia1, "10.00")));

        List<CotizacionesGraficaDTO> resultado = graficasService.obtenerGraficaCotizaciones("mes");

        assertEquals(LocalDateTime.now().toLocalDate().lengthOfMonth(), resultado.size());
        assertEquals(1L, resultado.get(0).getTotalCotizaciones());
    }

    // =====================================================================
    // obtenerGraficaOrdenes
    // =====================================================================

    @Test
    void graficaOrdenes_filtroHoy_tiene24PuntosYAcumulaPorHora() {
        LocalDateTime a = LocalDateTime.now().withHour(15).withMinute(0);
        stubOrdenesGrafica(List.of(ordenConMontoTotal(a, "400.00"), ordenConMontoTotal(a, "100.00")));

        List<OrdenesGraficaDTO> resultado = graficasService.obtenerGraficaOrdenes("dia");

        assertEquals(24, resultado.size());
        assertEquals(2L, resultado.get(15).getTotalOrdenes());
        assertMonto("500.00", resultado.get(15).getMontoTotal());
    }

    @Test
    void graficaOrdenes_filtroAnio_acumulaPorMes() {
        LocalDateTime enero = LocalDateTime.now().withDayOfMonth(1).withMonth(1);
        stubOrdenesGrafica(List.of(ordenConMontoTotal(enero, "1000.00")));

        List<OrdenesGraficaDTO> resultado = graficasService.obtenerGraficaOrdenes("anio");

        assertEquals(12, resultado.size());
        assertEquals(1L, resultado.get(0).getTotalOrdenes());
    }

    // =====================================================================
    // obtenerDashboardEgresos
    // =====================================================================

    @Test
    void dashboardEgresos_excluyeNullYAjustesDeEntrada_ySumaElResto() {
        LocalDateTime dia1 = LocalDateTime.now().withDayOfMonth(1);
        stubEgresos(List.of(
                mov(2, "100.00", dia1), // egreso normal -> cuenta
                mov(5, "-50.00", dia1), // ajuste de entrada -> se excluye
                mov(5, "30.00", dia1), // ajuste de egreso -> cuenta
                mov(3, null, dia1))); // monto null -> se excluye

        DashboardEgresosDTO resultado = graficasService.obtenerDashboardEgresos("mes");
        List<BarraEgresosDTO> barras = egrBarras(resultado);

        assertMonto("130.00", egrTotal(resultado));
        assertEquals(4, barras.size());
        assertEquals("Sem 1", egrBarraLabel(barras.get(0)));
        assertMonto("130.00", egrBarraValor(barras.get(0)));
        assertMonto("0", egrBarraValor(barras.get(1)));
    }

    @Test
    void dashboardEgresos_filtroNull_seTrataComoMes() {
        List<BarraEgresosDTO> barras = egrBarras(graficasService.obtenerDashboardEgresos(null));

        assertEquals(4, barras.size());
        ArgumentCaptor<LocalDateTime> inicio = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(movimientoRepository).findEgresosPorRango(eq(IDS_EGRESOS), inicio.capture(),
                any(LocalDateTime.class));
        assertEquals(1, inicio.getValue().getDayOfMonth());
    }

    @Test
    void dashboardEgresos_filtroSemana_agrupaPorDia() {
        LocalDateTime miercoles = LocalDateTime.now().with(DayOfWeek.WEDNESDAY);
        stubEgresos(List.of(mov(2, "200.00", miercoles)));

        List<BarraEgresosDTO> barras = egrBarras(graficasService.obtenerDashboardEgresos("semana"));

        assertEquals(7, barras.size());
        assertEquals("Mie", egrBarraLabel(barras.get(2)));
        assertMonto("200.00", egrBarraValor(barras.get(2)));
    }

    @Test
    void dashboardEgresos_filtroHoy_devuelveUnaBarraConElTotal() {
        stubEgresos(List.of(mov(2, "75.00", LocalDateTime.now())));

        DashboardEgresosDTO resultado = graficasService.obtenerDashboardEgresos("hoy");

        assertEquals(1, egrBarras(resultado).size());
        assertEquals("Hoy", egrBarraLabel(egrBarras(resultado).get(0)));
        assertMonto("75.00", egrTotal(resultado));
    }

    // =====================================================================
    // obtenerTablaFLujoCaja
    // =====================================================================

    @Test
    void tablaFlujoCaja_combinaIngresosYEgresos_clasificaAjustesYOrdenaPorFechaDesc() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 10, 12, 0);

        Orden o = orden(base.minusDays(1), "500.00");
        o.setIdOrden(1);
        o.setIdUsuario(4);

        Movimientos egreso = mov(2, "100.00", base.plusDays(3));
        egreso.setConcepto("Compra");
        Movimientos ajusteEntrada = mov(5, "-50.00", base.plusDays(2));
        ajusteEntrada.setConcepto("Ajuste +");
        Movimientos ajusteSalida = mov(5, "30.00", base.plusDays(1));
        ajusteSalida.setConcepto("Ajuste -");

        stubOrdenesGrafica(List.of(o));
        stubEgresos(List.of(egreso, ajusteEntrada, ajusteSalida));

        List<MovimientoTablaDTO> tabla = graficasService.obtenerTablaFLujoCaja("mes");

        assertEquals(4, tabla.size());
        // Orden descendente por fecha: egreso, ajusteEntrada, ajusteSalida, orden
        assertEquals("Compra", tablaConcepto(tabla.get(0)));
        assertEquals("Egreso", tablaTipo(tabla.get(0)));

        assertEquals("Ingreso (Ajuste)", tablaTipo(tabla.get(1)));
        assertMonto("50.00", tablaMonto(tabla.get(1))); // se vuelve positivo

        assertEquals("Egreso (Ajuste)", tablaTipo(tabla.get(2)));
        assertMonto("30.00", tablaMonto(tabla.get(2)));

        assertEquals("Orden #1", tablaConcepto(tabla.get(3)));
        assertEquals("Ingreso", tablaTipo(tabla.get(3)));
        assertMonto("500.00", tablaMonto(tabla.get(3)));
    }

    @Test
    void tablaFlujoCaja_sinDatos_devuelveListaVacia() {
        assertTrue(graficasService.obtenerTablaFLujoCaja(null).isEmpty());
    }

    // =====================================================================
    // obtenerFlujoCaja
    // =====================================================================

    @Test
    void flujoCaja_calculaIngresosEntradaEgresosYBalance() {
        LocalDateTime fecha = LocalDateTime.now();
        stubOrdenesGrafica(List.of(orden(fecha, "1000.00"), orden(fecha, "500.00"), orden(fecha, null)));
        stubEgresos(List.of(
                mov(2, "300.00", fecha), // egreso
                mov(5, "-100.00", fecha), // ajuste que entra a caja
                mov(5, "50.00", fecha))); // ajuste que sale de caja

        FlujoCajaDTO resultado = graficasService.obtenerFlujoCaja("mes");

        assertMonto("1500.00", flujoIngresos(resultado));
        assertMonto("100.00", flujoEntrada(resultado));
        assertMonto("350.00", flujoEgresos(resultado));
        assertMonto("1250.00", flujoBalance(resultado)); // 1500 + 100 - 350
    }

    @Test
    void flujoCaja_sinDatos_todoEnCero() {
        FlujoCajaDTO resultado = graficasService.obtenerFlujoCaja("semana");

        assertMonto("0", flujoIngresos(resultado));
        assertMonto("0", flujoEntrada(resultado));
        assertMonto("0", flujoEgresos(resultado));
        assertMonto("0", flujoBalance(resultado));
    }

    // =====================================================================
    // obtenerGraficaFinanzas
    // =====================================================================

    private GraficaFinanzasDTO buscarPunto(DashboardFinanzasDTO dto, String label) {
        return finGrafica(dto).stream()
                .filter(g -> label.equals(gLabel(g)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No existe el punto " + label));
    }

    @Test
    void graficaFinanzas_filtroHoy_tiene13HorasYCalculaBalance() {
        LocalDateTime a = LocalDateTime.now().withHour(10).withMinute(0);
        stubOrdenesGrafica(List.of(orden(a, "100.00"), orden(a, null)));
        stubEgresos(List.of(
                mov(2, "40.00", a),
                mov(5, "-20.00", a), // se ignora
                mov(3, null, a))); // se ignora

        DashboardFinanzasDTO resultado = graficasService.obtenerGraficaFinanzas("hoy");

        assertEquals(13, finGrafica(resultado).size()); // 08:00 a 20:00
        assertMonto("60.00", finBalance(resultado)); // 100 - 40
        GraficaFinanzasDTO punto = buscarPunto(resultado, "10:00");
        assertMonto("100.00", gIngresos(punto));
        assertMonto("40.00", gEgresos(punto));
    }

    @Test
    void graficaFinanzas_filtroMes_agrupaEnSemanasS1aS5() {
        LocalDateTime dia8 = LocalDateTime.now().withDayOfMonth(8);
        stubOrdenesGrafica(List.of(orden(dia8, "250.00")));

        DashboardFinanzasDTO resultado = graficasService.obtenerGraficaFinanzas("mes");

        assertEquals(5, finGrafica(resultado).size());
        assertMonto("250.00", gIngresos(buscarPunto(resultado, "S2")));
        assertMonto("250.00", finBalance(resultado));
    }

    @Test
    void graficaFinanzas_filtroNull_usaSemanaConSieteEtiquetas() {
        LocalDateTime lunes = LocalDateTime.now().with(DayOfWeek.MONDAY);
        stubOrdenesGrafica(List.of(orden(lunes, "90.00")));
        stubEgresos(List.of(mov(2, "30.00", lunes)));

        DashboardFinanzasDTO resultado = graficasService.obtenerGraficaFinanzas(null);

        assertEquals(7, finGrafica(resultado).size());
        assertMonto("60.00", finBalance(resultado));
    }
}