package com.imprenta.ordenes_service.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.CatCondicionesPagoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

import jakarta.transaction.Transactional;

@Service
public class MovimientosHelper {

    private final OrdenRepository ordenRepository;
    private final MovimientoRepository movimientoRepository;
    private final CatCondicionesPagoRepository condicionesPagoRepository;

    // ID fijo en tu BD para "Ingreso por Venta"
    private static final Integer TIPO_MOVIMIENTO_INGRESO = 1;

    @Autowired
    public MovimientosHelper(OrdenRepository ordenRepository,
            MovimientoRepository movimientoRepository,
            CatCondicionesPagoRepository condicionesPagoRepository) {
        this.ordenRepository = ordenRepository;
        this.movimientoRepository = movimientoRepository;
        this.condicionesPagoRepository = condicionesPagoRepository;
    }

    @Transactional
    public Movimientos registrarAbono(Integer idOrden, BigDecimal monto, String referencia, Integer idUsuario) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        Movimientos mov = new Movimientos();
        mov.setIdOrden(idOrden);
        mov.setMonto(monto);
        mov.setConcepto("Abono a Orden: " + referencia);
        mov.setIdTipoMovimiento(TIPO_MOVIMIENTO_INGRESO);
        mov.setIdUsuarioRegistro(idUsuario);
        mov.setFechaMovimiento(LocalDateTime.now());

        Movimientos movimientoGuardado = movimientoRepository.save(mov);

        BigDecimal pagadoActual = orden.getMontoPagado() == null ? BigDecimal.ZERO : orden.getMontoPagado();
        orden.setMontoPagado(pagadoActual.add(monto));

        ordenRepository.save(orden);

        return movimientoGuardado;
    }

    public BigDecimal calcularPagoMinimoRequerido(Orden orden) {
        BigDecimal total = orden.getMontoTotal();
        Integer idCondicion = orden.getIdCondicionPago();

        if (idCondicion == null)
            return total;

        CatCondicionesPago condicion = condicionesPagoRepository.findById(idCondicion)
                .orElseThrow(() -> new BadRequestException("Condicion de pago no encontrada"));

        Integer plazos = condicion.getNumeroPlazos();

        if (plazos <= 1) {
            return total;
        }

        return total.divide(new BigDecimal(plazos), 2, RoundingMode.CEILING);

    }

    public boolean cubreRequisitoMinimo(Orden orden) {
        BigDecimal pagado = orden.getMontoPagado() == null ? BigDecimal.ZERO : orden.getMontoPagado();
        BigDecimal minimo = calcularPagoMinimoRequerido(orden);

        return pagado.compareTo(minimo) >= 0;
    }

}
