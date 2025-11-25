package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.imprenta.ordenes_service.model.Movimientos;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimientos, Integer> {
    List<Movimientos> findByIdOrden(Integer idOrden);
}
