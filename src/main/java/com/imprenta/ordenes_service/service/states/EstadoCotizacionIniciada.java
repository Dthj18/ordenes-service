package com.imprenta.ordenes_service.service.states;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imprenta.ordenes_service.exception.BadRequestException;
import com.imprenta.ordenes_service.helpers.MovimientosHelper;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

@Component
public class EstadoCotizacionIniciada implements OrdenState {

    private static final Integer ID_ESTATUS_INICIADA = 1;
    private static final Integer ID_SIGUIENTE_PASO = 2;

    private final MovimientosHelper movimientosHelper;
    private final SecurityHelper securityHelper;

    @Autowired
    public EstadoCotizacionIniciada(MovimientosHelper movimientosHelper, SecurityHelper securityHelper) {
        this.movimientosHelper = movimientosHelper;
        this.securityHelper = securityHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ESTATUS_INICIADA;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_ADMIN,
                SecurityHelper.ROL_CONTADORA,
                SecurityHelper.ROL_MOSTRADOR);

        if (!movimientosHelper.cubreRequisitoMinimo(orden)) {
            BigDecimal minimo = movimientosHelper.calcularPagoMinimoRequerido(orden);
            BigDecimal pagado = orden.getMontoPagado() == null ? BigDecimal.ZERO : orden.getMontoPagado();

            throw new BadRequestException(
                    String.format("Falta pago. Pagado: $%s. Mínimo requerido: $%s", pagado, minimo));
        }

        int dias = orden.getPlazoEstimadoDias() == null ? 0 : orden.getPlazoEstimadoDias();

        LocalDate fechaEntrega = LocalDate.now().plusDays(dias);

        orden.setFechaEntregaFormal(fechaEntrega);

        return ID_SIGUIENTE_PASO;
    }

}
