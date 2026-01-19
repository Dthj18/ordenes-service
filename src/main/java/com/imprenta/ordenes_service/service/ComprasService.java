package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Hibernate; // 2. IMPORTA ESTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.CrearSolicitudDTO;
import com.imprenta.ordenes_service.dto.ProductoSolicitudDTO;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.DetalleSolicitud;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.SolicitudInsumo;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.SolicitudInsumoRepository;

import org.springframework.transaction.annotation.Transactional; // <--- OJO: Importa este, NO el de jakarta

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

    @Transactional
    public SolicitudInsumo crearSolicitud(CrearSolicitudDTO dto) {

        SolicitudInsumo solicitudPadre = new SolicitudInsumo();
        solicitudPadre.setIdUsuarioSolicitante(dto.getIdUsuario());
        solicitudPadre.setIdOrden(dto.getIdOrden());
        solicitudPadre.setIdEstatus(ESTATUS_PENDIENTE);
        solicitudPadre.setFechaCreacion(LocalDateTime.now());

        if (dto.getProductos() != null) {
            for (ProductoSolicitudDTO prod : dto.getProductos()) {

                DetalleSolicitud detalle = new DetalleSolicitud();

                detalle.setDescripcionPersonalizada(prod.getDescripcion());

                try {
                    int cant = Integer.parseInt(prod.getCantidad());
                    detalle.setCantidad(cant);
                } catch (NumberFormatException e) {
                    detalle.setCantidad(1);
                }

                solicitudPadre.agregarDetalle(detalle);
            }
        }

        return solicitudInsumoRepository.save(solicitudPadre);
    }

    @Transactional
    public Movimientos registrarCompra(Integer idSolicitud, BigDecimal costoReal, Integer idUsuarioAdmin) {

        securityHelper.validarPermiso(idUsuarioAdmin, SecurityHelper.ROL_ADMIN, SecurityHelper.ROL_DUENO,
                SecurityHelper.ROL_CONTADORA);

        // 1. Buscamos la solicitud (Esto trae al Padre y su lista de Detalles)
        SolicitudInsumo solicitud = solicitudInsumoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        solicitud.setIdEstatus(ESTATUS_COMPRADA);
        solicitudInsumoRepository.save(solicitud);

        // --- LÓGICA NUEVA PARA EL CONCEPTO ---
        // Construimos un resumen de qué se compró uniendo los detalles
        StringBuilder conceptoBuilder = new StringBuilder("Compra Solicitud #" + solicitud.getIdSolicitud() + ": ");

        if (solicitud.getDetalles() != null && !solicitud.getDetalles().isEmpty()) {
            // Recorremos la lista de hijos
            for (DetalleSolicitud det : solicitud.getDetalles()) {
                conceptoBuilder.append(det.getDescripcionPersonalizada())
                        .append(" (x").append(det.getCantidad()).append("), ");
            }
        }

        // Limpiamos el string y aseguramos que no exceda 255 caracteres (límite común
        // en BD)
        String conceptoFinal = conceptoBuilder.toString();
        if (conceptoFinal.length() > 250) {
            conceptoFinal = conceptoFinal.substring(0, 247) + "...";
        }
        // --------------------------------------

        Movimientos egreso = new Movimientos();
        egreso.setIdTipoMovimiento(TIPO_EGRESO);
        egreso.setMonto(costoReal.abs().negate());
        egreso.setConcepto(conceptoFinal); // Usamos el texto generado arriba
        egreso.setFechaMovimiento(LocalDateTime.now());
        egreso.setIdUsuarioRegistro(idUsuarioAdmin);
        egreso.setIdOrden(solicitud.getIdOrden());

        return movimientoRepository.save(egreso);
    }

    @Transactional(readOnly = true) // Mantenemos esto por seguridad
    public List<SolicitudInsumo> obtenerPendientes() {
        // 1. Buscamos los padres
        List<SolicitudInsumo> lista = solicitudInsumoRepository.findByIdEstatus(ESTATUS_PENDIENTE);

        // 2. FORZAMOS la carga de los hijos
        // Recorremos cada solicitud y "tocamos" la lista de detalles para que se
        // descargue
        for (SolicitudInsumo solicitud : lista) {
            Hibernate.initialize(solicitud.getDetalles());
        }

        return lista;
    }
}