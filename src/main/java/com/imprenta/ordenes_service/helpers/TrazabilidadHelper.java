package com.imprenta.ordenes_service.helpers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.model.CatEstatus;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.model.Trazabilidad;
import com.imprenta.ordenes_service.repository.CatEstatusRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;
import com.imprenta.ordenes_service.repository.TrazabilidadRepository;

import jakarta.transaction.Transactional;

@Service
public class TrazabilidadHelper {

    private final OrdenRepository ordenRepository;
    private final TrazabilidadRepository trazabilidadRepository;
    private final CatEstatusRepository catEstatusRepository;

    @Autowired
    public TrazabilidadHelper(OrdenRepository ordenRepository,
            TrazabilidadRepository trazabilidadRepository,
            CatEstatusRepository catEstatusRepository) {
        this.ordenRepository = ordenRepository;
        this.trazabilidadRepository = trazabilidadRepository;
        this.catEstatusRepository = catEstatusRepository;
    }

    @Transactional
    public Trazabilidad registrarCambio(Integer idOrden, String claveEstatus, Integer idUsuario) {
        CatEstatus estatus = catEstatusRepository.findByClave(claveEstatus)
                .orElseThrow(() -> new ResourceNotFoundException("Estatus no encontrado" + claveEstatus));

        Integer nuevoEstatusId = estatus.getIdEstatus();

        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada" + idOrden));

        orden.setIdEstatusActual(nuevoEstatusId);
        ordenRepository.save(orden);

        Trazabilidad nuevaTraza = new Trazabilidad();
        nuevaTraza.setIdOrden(idOrden);
        nuevaTraza.setIdEstatus(nuevoEstatusId);
        nuevaTraza.setIdUsuario(idUsuario);
        nuevaTraza.setFechaActualizacion(LocalDateTime.now());

        return trazabilidadRepository.save(nuevaTraza);
    }

    @Transactional
    public Trazabilidad registrarCambioPorId(Integer idOrden, Integer nuevoEstatusId, Integer idUsuario) {

        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setIdEstatusActual(nuevoEstatusId);
        ordenRepository.save(orden);

        Trazabilidad nuevaTraza = new Trazabilidad();
        nuevaTraza.setIdOrden(idOrden);
        nuevaTraza.setIdEstatus(nuevoEstatusId);
        nuevaTraza.setIdUsuario(idUsuario);
        nuevaTraza.setFechaActualizacion(LocalDateTime.now());

        return trazabilidadRepository.save(nuevaTraza);
    }
}
