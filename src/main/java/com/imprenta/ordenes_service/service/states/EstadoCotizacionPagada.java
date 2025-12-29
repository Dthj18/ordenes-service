package com.imprenta.ordenes_service.service.states;

import com.imprenta.ordenes_service.helpers.AlmacenHelper;
import com.imprenta.ordenes_service.helpers.SecurityHelper;
import com.imprenta.ordenes_service.model.Orden;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EstadoCotizacionPagada implements OrdenState {

    private final AlmacenHelper almacenHelper;
    private final SecurityHelper securityHelper;

    private static final Integer ID_ACTUAL = 2;
    private static final Integer ID_CON_INSUMOS = 3;
    private static final Integer ID_SIN_INSUMOS = 4;

    @Autowired
    public EstadoCotizacionPagada(SecurityHelper securityHelper,
            AlmacenHelper almacenHelper) {
        this.securityHelper = securityHelper;
        this.almacenHelper = almacenHelper;
    }

    @Override
    public Integer getIdEstatusRepresentado() {
        return ID_ACTUAL;
    }

    @Override
    public Integer procesarSiguientePaso(Orden orden, Integer idUsuario, Map<String, Object> params) {

        securityHelper.validarPermiso(idUsuario,
                SecurityHelper.ROL_DISENADOR,
                SecurityHelper.ROL_JEFE_TALLER);

        Boolean hayInsumos = (Boolean) params.get("hayInsumos");

        // Si el usuario no mandó nada (null), asumimos por defecto que SÍ hay insumos
        if (hayInsumos == null) {
            hayInsumos = true;
        }

        almacenHelper.procesarDecisionInsumos(orden, hayInsumos);

        if (hayInsumos) {
            return ID_CON_INSUMOS;
        } else {
            return ID_SIN_INSUMOS;
        }
    }
}