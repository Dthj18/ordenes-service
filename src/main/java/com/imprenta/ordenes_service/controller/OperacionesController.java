package com.imprenta.ordenes_service.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.dto.OrdenAsignadaDTO;
import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.OperacionesService;

@RestController
@RequestMapping("/api/operaciones")
public class OperacionesController {

    private final OperacionesService operacionesService;
    private final OrdenRepository ordenRepository;

    @Autowired
    public OperacionesController(OperacionesService operacionesService, OrdenRepository ordenRepository) {
        this.operacionesService = operacionesService;
        this.ordenRepository = ordenRepository;
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

    @GetMapping("/carga-trabajo-diseno")
    public ResponseEntity<Map<Integer, List<OrdenAsignadaDTO>>> obtenerCargaTrabajo() {

        List<OrdenAsignadaDTO> lista = ordenRepository.obtenerOrdenesPorDisenador();
        Map<Integer, List<OrdenAsignadaDTO>> agrupado = lista.stream()
                .collect(Collectors.groupingBy(OrdenAsignadaDTO::getIdDisenador));

        return ResponseEntity.ok(agrupado);
    }

}
