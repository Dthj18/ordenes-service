package com.imprenta.ordenes_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.imprenta.ordenes_service.dto.CrearOrdenDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenCardDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.helpers.CotizacionHelper;
import com.imprenta.ordenes_service.helpers.FileStorageHelper;
import com.imprenta.ordenes_service.helpers.MovimientosHelper;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.helpers.TrazabilidadHelper;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Trazabilidad;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.service.states.OrdenState;
import com.imprenta.ordenes_service.service.states.OrdenStateFactory;

import jakarta.transaction.Transactional;

@Service
public class OrdenService {
    private final OrdenRepository ordenRepository;
    private final CotizacionHelper cotizacionHelper;
    private final TrazabilidadHelper trazabilidadHelper;
    private final MovimientosHelper movimientosHelper;
    private final OrdenStateFactory stateFactory;
    private final FileStorageHelper fileStorageHelper;
    private final SecurityHelper securityHelper;

    private static final String ESTATUS_INICIAL = "COT_INICIADA";
    private static final Integer ID_ESTATUS_CANCELADA = 11;

    @Autowired
    public OrdenService(OrdenRepository ordenRepository,
            CotizacionHelper cotizacionHelper,
            TrazabilidadHelper trazabilidadHelper,
            MovimientosHelper movimientosHelper,
            OrdenStateFactory stateFactory,
            FileStorageHelper fileStorageHelper,
            SecurityHelper securityHelper) {
        this.ordenRepository = ordenRepository;
        this.cotizacionHelper = cotizacionHelper;
        this.trazabilidadHelper = trazabilidadHelper;
        this.movimientosHelper = movimientosHelper;
        this.stateFactory = stateFactory;
        this.fileStorageHelper = fileStorageHelper;
        this.securityHelper = securityHelper;
    }

    @Transactional
    public Orden crearCotizacion(CrearOrdenDTO dto) {
        Orden nuevaOrden = cotizacionHelper.generarCotizacion(
                dto.getOrden(),
                dto.getDetalles());

        trazabilidadHelper.registrarCambio(
                nuevaOrden.getIdOrden(),
                ESTATUS_INICIAL,
                dto.getIdUsuarioAccion());
        return nuevaOrden;
    }

    @Transactional
    public Movimientos registrarMovimiento(Integer idOrden, BigDecimal monto, String referencia, Integer idUsuario) {
        return movimientosHelper.registrarAbono(idOrden, monto, referencia, idUsuario);
    }

    @Transactional
    public Trazabilidad cambiarEstatusOrden(Integer idOrden, String nuevaClaveEstatus, Integer idUsuario) {
        return trazabilidadHelper.registrarCambio(idOrden, nuevaClaveEstatus, idUsuario);
    }

    public List<Orden> findAll() {
        return ordenRepository.findAll();
    }

    public Orden findById(Integer id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
    }

    @Transactional
    public Trazabilidad avanzarEstado(Integer idOrden, Integer idUsuario, Map<String, Object> params) {
        Orden orden = findById(idOrden);
        OrdenState estadoActual = stateFactory.obtenerEstado(orden.getIdEstatusActual());
        Integer idSiguientePaso = estadoActual.procesarSiguientePaso(orden, idUsuario, params);

        return trazabilidadHelper.registrarCambioPorId(idOrden, idSiguientePaso, idUsuario);
    }

    @Transactional
    public Orden asignarDisenador(Integer idOrden, Integer idUsuarioDisenador) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        // (Opcional) Aquí podrías validar con FeignClient si el usuario realmente es un
        // Diseñador

        orden.setIdUsuarioDisenador(idUsuarioDisenador);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden subirArchivoDiseno(Integer idOrden, MultipartFile file) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        String nombreArchivo = fileStorageHelper.guardarArchivo(file);

        orden.setRutaArchivo(nombreArchivo);
        orden.setFechaSubida(LocalDateTime.now());

        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden verificarInsumos(Integer idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setInsumosVerificados(true);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Trazabilidad cancelarOrden(Integer idOrden, Integer idUsuario, Integer idRazon) {

        securityHelper.validarPermiso(idUsuario, SecurityHelper.ROL_MOSTRADOR);

        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (orden.getIdEstatusActual().equals(12) || orden.getIdEstatusActual().equals(11)) {
            throw new BadRequestException("No se puede cancelar una orden finalizada o ya cancelada.");
        }
        orden.setIdRazonCancelacion(idRazon);
        return trazabilidadHelper.registrarCambioPorId(idOrden, ID_ESTATUS_CANCELADA, idUsuario);
    }

    public List<OrdenCardDTO> obtenerTarjetasMovil() {
        return ordenRepository.obtenerTarjetasMovil();
    }

}
