package com.imprenta.ordenes_service.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.dto.reportes.GraficaPastelDTO;
import com.imprenta.ordenes_service.dto.reportes.GraficaRadarDTO;
import com.imprenta.ordenes_service.repository.OrdenRepository;

@Service
public class GraficasService {

    private final OrdenRepository ordenRepository;

    @Autowired
    public GraficasService(OrdenRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    public Map<String, Object> obtenerDatosGraficasMovil(Integer idUsuario, LocalDate fecha) {
        Map<String, Object> respuesta = new HashMap<>();

        List<GraficaPastelDTO> pastel = ordenRepository.obtenerResumenEstatus(fecha);
        respuesta.put("datosPastel", pastel);

        List<GraficaRadarDTO> radar = ordenRepository.obtenerRazonesRechazo(fecha);
        respuesta.put("datosRadar", radar);

        return respuesta;
    }
}