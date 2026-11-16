package com.imprenta.ordenes_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.imprenta.ordenes_service.model.Orden;
import com.imprenta.ordenes_service.repository.DetalleOrdenRepository;
import com.imprenta.ordenes_service.repository.OrdenRepository;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@Transactional
public class OrdenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        ordenRepository.deleteAll();
    }

    @Test
    void debeCrearNuevaCotizacionYGuardarlaEnBaseDeDatos() throws Exception {

        stubFor(get(urlEqualTo("/api/clientes/322"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"idCliente\": 322, \"estatus\": \"ACTIVO\", \"limiteCredito\": 5000.00 }")));

        stubFor(get(urlEqualTo("/api/usuarios/8"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"idUsuario\": 8, \"idRol\": 2 }")));

        String requestBody = """
                {
                    "orden": {
                        "idCliente": 322,
                        "idUsuario": 8,
                        "montoTotal": 1500.00,
                        "requiereFactura": false,
                        "idCondicionPago": 1,
                        "rutaArchivo": "/archivos/diseno.pdf"
                    },
                    "detalles": [
                        {
                            "idProducto": 10,
                            "cantidad": 100,
                            "precioUnitario": 15.00,
                            "importe": 1500.00
                        }
                    ],
                    "idUsuarioAccion": 8
                }
                """;

        mockMvc.perform(post("/api/ordenes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());

        assertEquals(1, ordenRepository.findAll().size(), "La orden debió guardarse en la BD");
        // Reemplazamos la línea que daba NullPointerException por esta:
        assertEquals(1, detalleOrdenRepository.findAll().size(),
                "El detalle debió guardarse en cascada en la base de datos");
    }

    @Test
    void actualizarCondicionDePago() throws Exception {
        Orden orden = new Orden();
        orden.setIdUsuario(8);
        orden.setIdCliente(322);
        orden.setMontoTotal(new java.math.BigDecimal("1500.00"));
        orden.setRequiereFactura(false);
        orden.setIdCondicionPago(1);
        orden.setRutaArchivo("/archivos/diseno_original.pdf");

        Orden ordenSave = ordenRepository.save(orden);

        String requestBody = """
                {
                "idCondicionPago":3
                }
                """;

        mockMvc.perform(put("/api/ordenes/" + ordenSave.getIdOrden() + "/condicion-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        Orden ordenActualizada = ordenRepository.findById(ordenSave.getIdOrden()).orElseThrow();
        assertEquals(3, ordenActualizada.getIdCondicionPago(), "La condición de pago debió cambiar a 3");
    }

    @Test
    void cambiarEstatusOrden() throws Exception {
        Orden orden = new Orden();
        orden.setIdUsuario(8);
        orden.setIdCliente(322);
        orden.setMontoTotal(new java.math.BigDecimal("1500.00"));
        orden.setRequiereFactura(false);
        orden.setIdEstatusActual(1);
        orden.setRutaArchivo("/archivos/diseno_original.pdf");

        Orden ordenSave = ordenRepository.save(orden);

        String requestBody = """
                {
                    "claveEstatus": "COT_PAGADA"
                }
                """;

        mockMvc.perform(post("/api/ordenes/" + ordenSave.getIdOrden() + "/estatus")
                .header("X-User-Id", "8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());

        Orden ordenActualizada = ordenRepository.findById(ordenSave.getIdOrden()).orElseThrow();
        assertEquals(2, ordenActualizada.getIdEstatusActual());
    }

    @Test
    void debeRegistrarUnPagoYActualizarMontoPagadoDeLaOrden() throws Exception {
        stubFor(get(urlEqualTo("/api/usuarios/8"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"idUsuario\": 8, \"idRol\": 2 }")));

        Orden ordenFisica = new Orden();
        ordenFisica.setIdUsuario(8);
        ordenFisica.setIdCliente(322);
        ordenFisica.setMontoTotal(new java.math.BigDecimal("1500.00"));
        ordenFisica.setMontoPagado(new java.math.BigDecimal("0.00"));
        ordenFisica.setRequiereFactura(false);
        ordenFisica.setRutaArchivo("/archivos/diseno.pdf");

        Orden ordenGuardada = ordenRepository.save(ordenFisica);

        String requestBody = """
                {
                    "idCliente": 322,
                    "idOrden": %d,
                    "monto": 500.00,
                    "referencia": "SPEI-0987654321"
                }
                """.formatted(ordenGuardada.getIdOrden());

        mockMvc.perform(post("/api/ordenes/pagos")
                .header("X-User-Id", "8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());

        Orden ordenActualizada = ordenRepository.findById(ordenGuardada.getIdOrden()).orElseThrow();
        assertEquals(0, new java.math.BigDecimal("500.00").compareTo(ordenActualizada.getMontoPagado()),
                "El monto pagado en la orden debió actualizarse a 500.00");
    }

    @Test
    void debeRegistrarAbonoGlobalSinAsociarOrden() throws Exception {
        stubFor(get(urlEqualTo("/api/usuarios/8"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"idUsuario\": 8, \"idRol\": 2 }")));

        String requestBody = """
                {
                    "idCliente": 322,
                    "monto": 1000.00,
                    "referencia": "Transferencia - Abono libre a cuenta"
                }
                """;

        mockMvc.perform(post("/api/ordenes/pagos")
                .header("X-User-Id", "8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(322))
                .andExpect(jsonPath("$.idOrden").isEmpty())
                .andExpect(jsonPath("$.monto").value(1000.00));
    }

    @Test
    void debeBloquearPasoACreditoSiCliebteEstaAtrasado() throws Exception {
        stubFor(get(urlEqualTo("/api/usuarios/8"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"idUsuario\": 8, \"idRol\": 2 }")));

        stubFor(get(urlEqualTo("/api/clientes/322"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{ \"idCliente\": 322, \"estatusCredito\": \"ATRASADO\", \"limiteCredito\": 25000.00 }")));

        Orden orden = new Orden();
        orden.setIdUsuario(8);
        orden.setIdCliente(322);
        orden.setMontoTotal(new java.math.BigDecimal("5000.00"));
        orden.setIdEstatusActual(2);
        orden.setRequiereFactura(false);
        orden.setRutaArchivo("/archivos/banner.pdf");

        Orden ordenSave = ordenRepository.save(orden);

        String requestBody = """
                {
                    "idCondicionPago": 5
                }
                """;

        mockMvc.perform(put("/api/ordenes/" + ordenSave.getIdOrden() + "/condicion-pago")
                .header("X-User-Id", "8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}