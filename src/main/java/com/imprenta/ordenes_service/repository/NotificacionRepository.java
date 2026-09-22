package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imprenta.ordenes_service.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByReceptorIdAndLeidaFalseOrderByCreadaEnDesc(String receptorId);
}
