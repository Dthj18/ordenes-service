package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.SolicitudInsumoRepository;

import jakarta.transaction.Transactional;

@Service
public class ComprasService {

    private final SolicitudInsumoRepository solicitudInsumoRepository;
    private final MovimientoRepository movimientoRepository;
    private final SecurityHelper securityHelper;

    private static final Integer ESTATUS_PENDIENTE = 1;
    private static final Integer ESTATUS_COMPRADA = 2;

    private static final Integer TIPO_EGRESO = 2;

    @Autowired
    public ComprasService(SolicitudInsumoRepository solicitudInsumoRepository,
            MovimientoRepository movimientoRepository,
            SecurityHelper securityHelper) {
        this.solicitudInsumoRepository = solicitudInsumoRepository;
        this.movimientoRepository = movimientoRepository;
        this.securityHelper = securityHelper;
    }

    public SolicitudInsumo crearSolicitud(SolicitudInsumo solicitud, Integer idUsuario) {
        securityHelper.validarPermiso(idUsuario, SecurityHelper.ROL_JEFE_TALLER);

        solicitud.setIdUsuarioSolicitante(idUsuario);
        solicitud.setIdEstatus(ESTATUS_PENDIENTE);
        return solicitudInsumoRepository.save(solicitud);
    }

    @Transactional
    public Movimientos registrarCompra(Integer idSolicitud, BigDecimal costoReal, Integer idUsuarioAdmin) {

        securityHelper.validarPermiso(idUsuarioAdmin, SecurityHelper.ROL_ADMIN, SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_CONTADORA);

        SolicitudInsumo solicitud = solicitudInsumoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        solicitud.setIdEstatus(ESTATUS_COMPRADA);
        solicitudInsumoRepository.save(solicitud);

        Movimientos egreso = new Movimientos();
        egreso.setIdTipoMovimiento(TIPO_EGRESO);
        egreso.setMonto(costoReal.negate());
        egreso.setConcepto("Compra de material: " + solicitud.getDescripcionPersonalizada());
        egreso.setFechaMovimiento(LocalDateTime.now());
        egreso.setIdUsuarioRegistro(idUsuarioAdmin);
        egreso.setIdOrden(solicitud.getIdOrden());

        return movimientoRepository.save(egreso);
    }

    public List<SolicitudInsumo> obtenerPendientes() {
        return solicitudInsumoRepository.findByIdEstatus(ESTATUS_PENDIENTE);
    }

}
