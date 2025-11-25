package com.imprenta.ordenes_service.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.service.ComprasService;

@RestController
@RequestMapping("/api/compras")
public class ComprasController {

    private final ComprasService comprasService;

    @Autowired
    public ComprasController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @PostMapping("/solicitar")
    public ResponseEntity<SolicitudInsumo> crearSolicitud(@RequestBody Map<String, Object> body) {

        Integer idUsuario = (Integer) body.get("idUsuario");
        String descripcion = (String) body.get("descripcion");
        Integer cantidad = (Integer) body.get("cantidad");
        Integer irOrden = (Integer) body.get("idOrden");

        SolicitudInsumo solicitud = new SolicitudInsumo();
        solicitud.setDescripcionPersonalizada(descripcion);
        solicitud.setCantidad(cantidad);
        solicitud.setIdOrden(irOrden);

        SolicitudInsumo nuevaSolicitud = comprasService.crearSolicitud(solicitud, idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
    }

    @GetMapping("/{idSolicitud}/comprar")
    public ResponseEntity<Movimientos> registrarCompra(
            @PathVariable Integer idSolicitud,
            @RequestBody Map<String, Object> body) {
        Integer idUsuario = (Integer) body.get("idUsuario");
        BigDecimal costoReal = new BigDecimal(body.get("costoReal").toString());

        Movimientos egreso = comprasService.registrarCompra(idSolicitud, costoReal, idUsuario);

        return ResponseEntity.ok(egreso);
    }

}
