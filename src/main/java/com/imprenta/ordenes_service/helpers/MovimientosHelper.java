package com.imprenta.ordenes_service.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.exception.ResourceNotFoundException;
import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.Movimientos;
import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.CatCondicionesPagoRepository;
import com.imprenta.ordenes_service.repository.MovimientoRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@Service
public class MovimientosHelper {

    private final OrdenRepository ordenRepository;
    private final MovimientoRepository movimientoRepository;
    private final CatCondicionesPagoRepository condicionesPagoRepository;
    private final SecurityHelper securityHelper;

    private static final Integer TIPO_MOVIMIENTO_INGRESO = 1;

    @Autowired
    public MovimientosHelper(OrdenRepository ordenRepository,
            MovimientoRepository movimientoRepository,
            CatCondicionesPagoRepository condicionesPagoRepository,
            SecurityHelper securityHelper) {
        this.ordenRepository = ordenRepository;
        this.movimientoRepository = movimientoRepository;
        this.condicionesPagoRepository = condicionesPagoRepository;
        this.securityHelper = securityHelper;
    }

    @Transactional
    public Movimientos registrarAbono(Integer idOrden, BigDecimal monto, String referencia, Integer idUsuario) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_MOSTRADOR,
                SecurityHelper.ROL_CONTADORA,
                SecurityHelper.ROL_ADMIN,
                SecurityHelper.ROL_DUENO);

        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + idOrden));

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto del abono debe ser mayor a 0.");
        }

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

        // (Opcional) Aquí podrías validar que no pague de más:
        // if (orden.getMontoPagado().compareTo(orden.getMontoTotal()) > 0) { ... error
        // ... }

        ordenRepository.save(orden);

        return movimientoGuardado;
    }

    public BigDecimal calcularPagoMinimoRequerido(Orden orden) {
        BigDecimal total = orden.getMontoTotal();
        Integer idCondicion = orden.getIdCondicionPago();

        if (idCondicion == null)
            return total;

        CatCondicionesPago condicion = condicionesPagoRepository.findById(idCondicion)
                .orElseThrow(() -> new BadRequestException("Condición de pago no encontrada en el catálogo."));

        Integer plazos = condicion.getNumeroPlazos();

        if (plazos == null || plazos <= 1) {
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