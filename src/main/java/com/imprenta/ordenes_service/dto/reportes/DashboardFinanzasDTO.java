package com.imprenta.ordenes_service.dto.reportes;

import java.math.BigDecimal;
import java.util.List;

public class DashboardFinanzasDTO {
    private BigDecimal montoPagado;
    private List<BarraGraficaDTO> datosGrafica;

    public DashboardFinanzasDTO(BigDecimal montoPagado, List<BarraGraficaDTO> datosGrafica) {
        this.montoPagado = montoPagado;
        this.datosGrafica = datosGrafica;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public List<BarraGraficaDTO> getDatosGrafica() {
        return datosGrafica;
    }

    public void setDatosGrafica(List<BarraGraficaDTO> datosGrafica) {
        this.datosGrafica = datosGrafica;
    }

}
