package com.imprenta.ordenes_service.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imprenta.ordenes_service.config.MqttGateway;
import com.imprenta.ordenes_service.model.Notificacion;
import com.imprenta.ordenes_service.repository.NotificacionRepository;

@ExtendWith(MockitoExtension.class)
class NotificacionesServiceTest {

    private static final String TOPIC = "imprenta/notificaciones";

    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private MqttGateway mqttGateway;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "defaultTopic", TOPIC);
    }

    private boolean estaLeida(Notificacion n) {
        return Boolean.TRUE.equals(n.getLeida());
    }

    // ---------- notificarARoles ----------

    @Test
    void notificarARoles_conDestinatarios_guardaYPublicaUnaNotificacionPorCada() {
        List<String> roles = List.of("CONTADORA", "DUEÑO");
        when(notificacionRepository.findIdsUsuariosPorRoles(roles)).thenReturn(List.of("10", "11"));

        notificationService.notificarARoles("3", roles, "ORDEN_CANCELADA", "Titulo", "Mensaje");

        verify(notificacionRepository, times(2)).save(any(Notificacion.class));
        verify(mqttGateway, times(2)).sendMqtt(anyString(), anyString());

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, times(2)).save(captor.capture());
        assertEquals("10", captor.getAllValues().get(0).getReceptorId());
        assertEquals("11", captor.getAllValues().get(1).getReceptorId());
    }

    @Test
    void notificarARoles_listaVacia_noGuardaNiPublica() {
        List<String> roles = List.of("DISEÑADOR");
        when(notificacionRepository.findIdsUsuariosPorRoles(roles)).thenReturn(List.of());

        notificationService.notificarARoles("3", roles, "CAT", "T", "M");

        verify(notificacionRepository, never()).save(any());
        verifyNoInteractions(mqttGateway);
    }

    @Test
    void notificarARoles_listaNull_noGuardaNiPublica() {
        List<String> roles = List.of("DISEÑADOR");
        when(notificacionRepository.findIdsUsuariosPorRoles(roles)).thenReturn(null);

        notificationService.notificarARoles("3", roles, "CAT", "T", "M");

        verify(notificacionRepository, never()).save(any());
        verifyNoInteractions(mqttGateway);
    }

    // ---------- procesarYNotificar ----------

    @Test
    void procesarYNotificar_guardaNotificacionNoLeidaConLosDatosRecibidos() {
        notificationService.procesarYNotificar("3", "10", "ORDEN_CANCELADA", "Alerta", "Orden cancelada");

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        Notificacion guardada = captor.getValue();

        assertEquals("3", guardada.getEmisorId());
        assertEquals("10", guardada.getReceptorId());
        assertEquals("ORDEN_CANCELADA", guardada.getCategoria());
        assertEquals("Alerta", guardada.getTitulo());
        assertEquals("Orden cancelada", guardada.getMensaje());
        assertFalse(estaLeida(guardada));
    }

    @Test
    void procesarYNotificar_publicaJsonEnElTopicPorDefecto() throws Exception {
        notificationService.procesarYNotificar("3", "10", "ORDEN_CANCELADA", "Alerta", "Orden cancelada");

        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> topic = ArgumentCaptor.forClass(String.class);
        verify(mqttGateway).sendMqtt(json.capture(), topic.capture());

        assertEquals(TOPIC, topic.getValue());

        JsonNode nodo = objectMapper.readTree(json.getValue());
        assertEquals("3", nodo.get("emisorId").asText());
        assertEquals("10", nodo.get("receptorId").asText());
        assertEquals("ORDEN_CANCELADA", nodo.get("categoria").asText());
        assertEquals("Alerta", nodo.get("contenido").get("titulo").asText());
        assertEquals("Orden cancelada", nodo.get("contenido").get("mensaje").asText());
    }

    @Test
    void procesarYNotificar_siFallaMqtt_noRelanzaYLaNotificacionYaSeGuardo() {
        doThrow(new RuntimeException("Broker caído"))
                .when(mqttGateway).sendMqtt(anyString(), anyString());

        assertDoesNotThrow(() -> notificationService.procesarYNotificar("3", "10", "CAT", "T", "M"));

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    // ---------- obtenerPendientes ----------

    @Test
    void obtenerPendientes_devuelveLoQueEntregaElRepositorio() {
        List<Notificacion> pendientes = List.of(new Notificacion(), new Notificacion());
        when(notificacionRepository.findByReceptorIdAndLeidaFalseOrderByCreadaEnDesc("10"))
                .thenReturn(pendientes);

        assertSame(pendientes, notificationService.obtenerPendientes("10"));
    }

    // ---------- marcarComoLeida ----------

    @Test
    void marcarComoLeida_existente_marcaLeidaConFechaYGuarda() {
        Notificacion notificacion = new Notificacion();
        notificacion.setLeida(false);
        when(notificacionRepository.findById(5L)).thenReturn(Optional.of(notificacion));

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        notificationService.marcarComoLeida(5L);

        assertTrue(estaLeida(notificacion));
        assertNotNull(notificacion.getLeidaEn());
        assertTrue(notificacion.getLeidaEn().isAfter(antes));
        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void marcarComoLeida_inexistente_lanzaExcepcionConElId() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.marcarComoLeida(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(notificacionRepository, never()).save(any());
    }
}