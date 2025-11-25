package com.imprenta.ordenes_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.service.OperacionesService;

@RestController
@RequestMapping("/api/operaciones")
public class OperacionesController {

    private final OperacionesService operacionesService;

    @Autowired
    public OperacionesController(OperacionesService operacionesService) {
        this.operacionesService = operacionesService;
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam(required = false, defaultValue = "") String q) {
        List<Producto> productos = operacionesService.buscarProductos(q);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/condiciones-pago")
    public ResponseEntity<List<CatCondicionesPago>> obtenerCondiciones() {
        return ResponseEntity.ok(operacionesService.obtenerTodasLasCondiciones());
    }

}
