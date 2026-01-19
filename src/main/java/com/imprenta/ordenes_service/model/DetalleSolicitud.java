package com.imprenta.ordenes_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Detalles_Solicitud_Insumo")
public class DetalleSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @Column(name = "id_insumo")
    private Integer idInsumo;

    @Column(name = "descripcion_personalizada")
    private String descripcionPersonalizada;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    // --- Relación inversa hacia el Padre ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud") // FK en la base de datos
    @JsonIgnore // Importante para evitar errores al convertir a JSON
    private SolicitudInsumo solicitud;

    public DetalleSolicitud() {
    }

    // Getters y Setters
    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
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

    public SolicitudInsumo getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudInsumo solicitud) {
        this.solicitud = solicitud;
    }
}