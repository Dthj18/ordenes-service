package com.imprenta.ordenes_service.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.ComprasService;

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
            logger.warn(
                    "ALERTA DE STOCK: Faltan insumos para la Orden #{}. Se requiere generar solicitud de compra.",
                    orden.getIdOrden());

            SolicitudInsumo solicitud = new SolicitudInsumo();
            solicitud.setDescripcionPersonalizada("Material faltante para Orden #" + orden.getIdOrden());
            solicitud.setCantidad(1);
            solicitud.setIdOrden(orden.getIdOrden());

            comprasService.crearSolicitud(solicitud, 6);

            logger.info("📝 Solicitud de compra generada automáticamente.");
        }
    }

}
