package com.imprenta.ordenes_service.helpers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.model.DetalleOrden;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.DetalleOrdenRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

import jakarta.transaction.Transactional;

@Service
public class CotizacionHelper {

    private final OrdenRepository ordenRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    public CotizacionHelper(OrdenRepository ordenRepository,
            DetalleOrdenRepository detalleOrdenRepository) {
        this.ordenRepository = ordenRepository;
        this.detalleOrdenRepository = detalleOrdenRepository;
    }

    @Transactional
    public Orden generarCotizacion(Orden orden, List<DetalleOrden> detalles) {

        if (orden == null || detalles == null || detalles.isEmpty()) {
            throw new BadRequestException("Faltan datos para la cotización");
        }

        BigDecimal totalCalculado = calcularTotal(detalles);
        orden.setMontoTotal(totalCalculado);

        orden.setIdOrden(null);
        if (orden.getMontoPagado() == null) {
            orden.setMontoPagado(BigDecimal.ZERO);
        }

        Orden ordenGuardada = ordenRepository.save(orden);

        guardarDetallesDeLaOrden(ordenGuardada.getIdOrden(), detalles);

        return ordenGuardada;
    }

    private void guardarDetallesDeLaOrden(Integer idOrden, List<DetalleOrden> detalles) {
        for (DetalleOrden detalle : detalles) {
            detalle.setIdOrden(idOrden);
            detalle.setIdDetalle(null);
            detalleOrdenRepository.save(detalle);
        }
    }

    private BigDecimal calcularTotal(List<DetalleOrden> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleOrden detalle : detalles) {
            if (detalle.getImporte() != null) {
                total = total.add(detalle.getImporte());
            }
        }
        return total;
    }

}
