package com.imprenta.ordenes_service.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.dto.reportes.DashboardFinanzasDTO;
import com.imprenta.ordenes_service.dto.reportes.DashboardResumenDTO;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.service.GraficasService;

@RestController
@RequestMapping("/api/dashboard")
public class GraficasController {

    private final SecurityHelper securityHelper;
    private final GraficasService graficasService;

    @Autowired
    public GraficasController(SecurityHelper securityHelper, GraficasService graficasService) {
        this.securityHelper = securityHelper;
        this.graficasService = graficasService;
    }

    @GetMapping("/movil/graficas")
    public ResponseEntity<Map<String, Object>> getGraficasMovil(
            @RequestParam Integer idUsuario,
            @RequestParam(name = "fechaInicio", required = false) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false) LocalDate fechaFin) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_ADMIN);

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;

        return ResponseEntity.ok(graficasService.obtenerDatosGraficasMovil(idUsuario, inicio, fin));
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen() {
        return ResponseEntity.ok(graficasService.obtenerResumen());
    }

    @GetMapping("/finanzas")
    public ResponseEntity<DashboardFinanzasDTO> getFinanzas(@RequestParam String filtro) {
        return ResponseEntity.ok(graficasService.obtenerFinanzas(filtro));
    }

}
