package com.imprenta.ordenes_service.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Map<String, Object>> getGraficasMovil(@RequestParam Integer idUsuario, @RequestParam(required = false) LocalDate fecha) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_ADMIN);

        return ResponseEntity.ok(graficasService.obtenerDatosGraficasMovil(idUsuario, fecha));
    }

}
