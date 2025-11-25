package com.imprenta.ordenes_service.dto;

public class CambiarEstatusDTO {

    private String claveEstatus;
    private Integer idUsuario;

    public CambiarEstatusDTO() {

    }

    public String getClaveEstatus() {
        return claveEstatus;
    }

    public void setClaveEstatus(String claveEstatus) {
        this.claveEstatus = claveEstatus;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

}
