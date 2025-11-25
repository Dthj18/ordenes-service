package com.imprenta.ordenes_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Solicitudes_Insumos")
public class SolicitudInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "id_usuario_solicitante", nullable = false)
    private Integer idUsuarioSolicitante;

    @Column(name = "id_insumo")
    private Integer idInsumo;

    @Column(name = "descripcion_personalizada")
    private String descripcionPersonalizada;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "id_estatus", nullable = false)
    private Integer idEstatus;

    public SolicitudInsumo() {
    }

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public Integer getIdUsuarioSolicitante() {
        return idUsuarioSolicitante;
    }

    public void setIdUsuarioSolicitante(Integer idUsuarioSolicitante) {
        this.idUsuarioSolicitante = idUsuarioSolicitante;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(Integer idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getDescripcionPersonalizada() {
        return descripcionPersonalizada;
    }

    public void setDescripcionPersonalizada(String descripcionPersonalizada) {
        this.descripcionPersonalizada = descripcionPersonalizada;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(Integer idEstatus) {
        this.idEstatus = idEstatus;
    }

}
