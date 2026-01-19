package com.imprenta.ordenes_service.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
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

    // --- CAMBIO PRINCIPAL AQUÍ ---
    @PostMapping("/solicitar")
    public ResponseEntity<?> crearSolicitud(@RequestBody CrearSolicitudDTO dto) {
        try {
            // Validamos que venga al menos un producto
            if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
                return ResponseEntity.badRequest().body("La lista de productos no puede estar vacía");
            }

            // AHORA el servicio devuelve UN SOLO objeto (La solicitud Padre)
            SolicitudInsumo solicitudGuardada = comprasService.crearSolicitud(dto);

            Map<String, Object> respuesta = new HashMap<>();
            
            // Podemos contar cuántos hijos tiene para el mensaje
            int cantidadItems = (solicitudGuardada.getDetalles() != null) ? solicitudGuardada.getDetalles().size() : 0;
            
            respuesta.put("mensaje", "Solicitud #" + solicitudGuardada.getIdSolicitud() + " creada con " + cantidadItems + " productos.");
            respuesta.put("solicitud", solicitudGuardada); // Devolvemos el objeto completo con sus hijos

            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    // Este se queda prácticamente igual, solo asegura la conversión de tipos
    @PostMapping("/{idSolicitud}/comprar")
    public ResponseEntity<Movimientos> registrarCompra(
            @PathVariable Integer idSolicitud,
            @RequestBody Map<String, Object> body) {
        
        // Es mejor práctica castear con seguridad
        Integer idUsuario = Integer.valueOf(body.get("idUsuario").toString());
        BigDecimal costoReal = new BigDecimal(body.get("costoReal").toString());

        Movimientos egreso = comprasService.registrarCompra(idSolicitud, costoReal, idUsuario);

        return ResponseEntity.ok(egreso);
    }

    // Este también sigue igual, devolverá la lista de solicitudes (Padres)
    // y cada una traerá adentro su lista de detalles (Hijos)
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudInsumo>> verPendientes() {
        return ResponseEntity.ok(comprasService.obtenerPendientes());
    }

}