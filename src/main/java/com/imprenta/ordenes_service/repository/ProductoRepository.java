package com.imprenta.ordenes_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imprenta.ordenes_service.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByDescripcionContainingIgnoreCase(String termino);

}
