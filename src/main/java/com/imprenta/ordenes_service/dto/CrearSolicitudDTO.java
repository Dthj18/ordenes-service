package com.imprenta.ordenes_service.dto;

import java.util.List;

public class CrearSolicitudDTO {
    private Integer idUsuario;
    private Integer idOrden;
    private String observaciones;
    private List<ProductoSolicitudDTO> productos;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ProductoSolicitudDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoSolicitudDTO> productos) {
        this.productos = productos;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

}
