package com.imprenta.ordenes_service.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
import com.imprenta.ordenes_service.dto.ProductoSolicitudDTO;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.ComprasService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AlmacenHelper {

    private static final Logger logger = LoggerFactory.getLogger(AlmacenHelper.class);
    private final OrdenRepository ordenRepository;
    private final ComprasService comprasService;

    @Autowired
    public AlmacenHelper(OrdenRepository ordenRepository, ComprasService comprasService) {
        this.ordenRepository = ordenRepository;
        this.comprasService = comprasService;
    }

    @Transactional
    public void procesarDecisionInsumos(Orden orden, boolean hayInsumos) {

        orden.setInsumosVerificados(hayInsumos);
        ordenRepository.save(orden);

        if (hayInsumos) {
            logger.info("Material reservado exitosamente para la Orden #{}", orden.getIdOrden());

        } else {
            logger.warn("ALERTA DE STOCK: Faltan insumos para la Orden #{}. Se requiere generar solicitud de compra.",
                    orden.getIdOrden());

            ProductoSolicitudDTO itemDTO = new ProductoSolicitudDTO();
            itemDTO.setDescripcion("Material faltante para Orden #" + orden.getIdOrden());
            itemDTO.setCantidad("1");

            CrearSolicitudDTO solicitudDTO = new CrearSolicitudDTO();
            solicitudDTO.setIdUsuario(6);
            solicitudDTO.setObservaciones("Generado automáticamente por falta de stock");

            // ⚠️ IMPORTANTE: Si quieres guardar el ID de la orden, agrégalo al DTO (ver
            // abajo)
            // solicitudDTO.setIdOrden(orden.getIdOrden());

            solicitudDTO.setProductos(List.of(itemDTO));

            comprasService.crearSolicitud(solicitudDTO);

            logger.info("Solicitud de compra generada automáticamente.");
        }
    }

}
