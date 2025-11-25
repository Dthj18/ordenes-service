package com.imprenta.ordenes_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.model.CatCondicionesPago;
import com.imprenta.ordenes_service.model.Producto;
import com.imprenta.ordenes_service.repository.CatCondicionesPagoRepository;
import com.imprenta.ordenes_service.repository.ProductoRepository;

@Service
public class OperacionesService {

    private final ProductoRepository productoRepository;
    private final CatCondicionesPagoRepository condicionesPagoRepository;

    @Autowired
    public OperacionesService(ProductoRepository productoRepository,
            CatCondicionesPagoRepository condicionesPagoRepository) {
        this.productoRepository = productoRepository;
        this.condicionesPagoRepository = condicionesPagoRepository;
    }

    public List<Producto> buscarProductos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return productoRepository.findAll(); // O devuelve lista vacía si son muchos
        }
        return productoRepository.findByDescripcionContainingIgnoreCase(termino);
    }

    public List<CatCondicionesPago> obtenerTodasLasCondiciones() {
        return condicionesPagoRepository.findAll();
    }
}
