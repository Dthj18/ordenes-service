package com.imprenta.ordenes_service.dto.reportes;

public class DashboardResumenDTO {
    private Long cotizacionesMes;
    private Long ordenesActivas;
    private Long pagosPendientes;

    public DashboardResumenDTO(Long cotizacionesMes, Long ordenesActivas, Long pagosPendientes) {
        this.cotizacionesMes = cotizacionesMes;
        this.ordenesActivas = ordenesActivas;
        this.pagosPendientes = pagosPendientes;
    }

    public Long getCotizacionesMes() {
        return cotizacionesMes;
    }

    public void setCotizacionesMes(Long cotizacionesMes) {
        this.cotizacionesMes = cotizacionesMes;
    }

    public Long getOrdenesActivas() {
        return ordenesActivas;
    }

    public void setOrdenesActivas(Long ordenesActivas) {
        this.ordenesActivas = ordenesActivas;
    }

    public Long getPagosPendientes() {
        return pagosPendientes;
    }

    public void setPagosPendientes(Long pagosPendientes) {
        this.pagosPendientes = pagosPendientes;
    }

}
