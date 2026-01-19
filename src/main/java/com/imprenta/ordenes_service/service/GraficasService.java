package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.reportes.BarraGraficaDTO;
import com.imprenta.ordenes_service.dto.reportes.DashboardFinanzasDTO;
import com.imprenta.ordenes_service.dto.reportes.DashboardResumenDTO;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@Service
public class GraficasService {

    private final OrdenRepository ordenRepository;

    @Autowired
    public GraficasService(OrdenRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    public Map<String, Object> obtenerDatosGraficasMovil(
            Integer idUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        Map<String, Object> respuesta = new HashMap<>();

        var pastel = ordenRepository.obtenerResumenEstatus(fechaInicio, fechaFin);
        respuesta.put("datosPastel", pastel);

        var radar = ordenRepository.obtenerRazonesRechazo(fechaInicio, fechaFin);
        respuesta.put("datosRadar", radar);

        return respuesta;
    }

    public DashboardResumenDTO obtenerResumen() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMes = ahora.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime finMes = ahora.with(LocalTime.MAX);

        Long totalCotizaciones = ordenRepository.contarCotizacionesMes(inicioMes, finMes);
        Long ordenesActivas = ordenRepository.contarOrdenesActivas();
        Long pagosPendientes = ordenRepository.contarPagosPendientes();

        return new DashboardResumenDTO(totalCotizaciones, ordenesActivas, pagosPendientes);
    }

    public DashboardFinanzasDTO obtenerFinanzas(String filtro) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora;
        LocalDateTime fin = ahora;

        // 1. Configuración de Fechas
        if ("Hoy".equalsIgnoreCase(filtro)) {
            inicio = ahora.with(LocalTime.MIN);
            fin = ahora.with(LocalTime.MAX);
        } else if ("Semana".equalsIgnoreCase(filtro)) {
            // Lunes a Domingo de la semana actual
            inicio = ahora.with(java.time.DayOfWeek.MONDAY).with(LocalTime.MIN);
            fin = ahora.with(java.time.DayOfWeek.SUNDAY).with(LocalTime.MAX);
        } else if ("Mes".equalsIgnoreCase(filtro)) {
            // Todo el mes actual (Del 1 al último día)
            inicio = ahora.withDayOfMonth(1).with(LocalTime.MIN);
            fin = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        }

        // 2. Consulta a BD
        List<Orden> ordenes = ordenRepository.encontrarOrdenesParaFinanzas(inicio, fin);

        // 🔍 DEBUG: Mira esto en la consola de IntelliJ/Spring
        System.out.println("Filtro: " + filtro);
        System.out.println("Rango: " + inicio + " a " + fin);
        System.out.println("Órdenes encontradas: " + ordenes.size());

        // 3. Calcular Total (Blindado contra nulos)
        BigDecimal total = ordenes.stream()
                .map(o -> o.getMontoPagado() != null ? o.getMontoPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Generar Barras
        List<BarraGraficaDTO> barras = new ArrayList<>();

        if ("Semana".equalsIgnoreCase(filtro)) {
            // Lógica por Días (Lun-Dom)
            String[] dias = { "Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom" };
            for (int i = 0; i < 7; i++) {
                int diaSemana = i + 1;
                double sumaDia = ordenes.stream()
                        .filter(o -> o.getFechaCreacion().getDayOfWeek().getValue() == diaSemana)
                        .mapToDouble(o -> o.getMontoPagado() != null ? o.getMontoPagado().doubleValue() : 0.0)
                        .sum();

                String color = (ahora.getDayOfWeek().getValue() == diaSemana ? "#5C7CFA" : "#E0E7FF");
                barras.add(new BarraGraficaDTO(sumaDia, dias[i], color));
            }
        } else if ("Mes".equalsIgnoreCase(filtro)) {
            // Lógica por Semanas (Sem 1, Sem 2, Sem 3, Sem 4)
            for (int i = 0; i < 4; i++) {
                // Semana 1 (Días 1-7), Semana 2 (8-14), etc.
                int diaInicio = (i * 7) + 1;
                int diaFin = (i + 1) * 7;

                double sumaSemana = ordenes.stream()
                        .filter(o -> {
                            int diaMes = o.getFechaCreacion().getDayOfMonth();
                            return diaMes >= diaInicio && diaMes <= diaFin;
                        })
                        .mapToDouble(o -> o.getMontoPagado() != null ? o.getMontoPagado().doubleValue() : 0.0)
                        .sum();

                // Color: Azul si la fecha actual cae en esta semana
                int diaHoy = ahora.getDayOfMonth();
                String color = (diaHoy >= diaInicio && diaHoy <= diaFin) ? "#5C7CFA" : "#E0E7FF";

                barras.add(new BarraGraficaDTO(sumaSemana, "Sem " + (i + 1), color));
            }
        } else {
            // "Hoy" -> Muestra una sola barra
            barras.add(new BarraGraficaDTO(total.doubleValue(), "Hoy", "#5C7CFA"));
        }

        return new DashboardFinanzasDTO(total, barras);
    }
}