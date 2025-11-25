package com.imprenta.ordenes_service.helpers;

import java.util.Arrays;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imprenta.ordenes_service.client.PersonasClient;
import com.imprenta.ordenes_service.dto.UsuarioDTO;
import com.imprenta.ordenes_service.exception.BadRequestException;

@Service
public class SecurityHelper {

    private final PersonasClient personasClient;

    // CONSTANTES DE ROLES (Según tu SQL)
    public static final Integer ROL_DUENO = 1;
    public static final Integer ROL_ADMIN = 2;
    public static final Integer ROL_CONTADORA = 3;
    public static final Integer ROL_MOSTRADOR = 4;
    public static final Integer ROL_DISENADOR = 5;
    public static final Integer ROL_JEFE_TALLER = 6;

    @Autowired
    public SecurityHelper(PersonasClient personasClient) {
        this.personasClient = personasClient;
    }

    /**
     * Valida si el usuario tiene uno de los roles permitidos.
     * Si no tiene permiso, lanza una excepción y detiene el proceso.
     */
    public void validarPermiso(Integer idUsuario, Integer... rolesPermitidos) {

        // 1. Obtener información del usuario (Llamada remota)
        UsuarioDTO usuario;
        try {
            usuario = personasClient.obtenerUsuarioPorId(idUsuario);
        } catch (Exception e) {
            throw new BadRequestException("Error de seguridad: No se pudo validar la identidad del usuario.");
        }

        Integer rolUsuario = usuario.getIdRol();

        // 2. El Dueño y Admin SIEMPRE tienen permiso (Superusuarios)
        if (rolUsuario.equals(ROL_DUENO) || rolUsuario.equals(ROL_ADMIN)) {
            return; // Pásale
        }

        // 3. Verificar si el rol del usuario está en la lista de permitidos
        List<Integer> permitidos = Arrays.asList(rolesPermitidos);
        if (!permitidos.contains(rolUsuario)) {
            throw new BadRequestException("ACCESO DENEGADO: El usuario '" + usuario.getNombre() +
                    "' no tiene los permisos necesarios para realizar esta acción.");
        }
    }

    // Método extra para validar que sea EL diseñador asignado
    public void validarEsDiseñadorAsignado(Integer idUsuario, Integer idAsignado) {
        UsuarioDTO usuario = personasClient.obtenerUsuarioPorId(idUsuario);
        Integer rol = usuario.getIdRol();

        // Si es Admin/Dueño, pasa.
        if (rol.equals(ROL_DUENO) || rol.equals(ROL_ADMIN))
            return;

        // Si es diseñador, debe ser el asignado
        if (rol.equals(ROL_DISENADOR)) {
            if (idAsignado == null || !idAsignado.equals(idUsuario)) {
                throw new BadRequestException("ACCESO DENEGADO: Solo el diseñador asignado a esta orden puede avanzar.");
            }
        } else {
            // Si no es ni diseñador ni admin
            throw new BadRequestException("ACCESO DENEGADO: Se requieren permisos de diseño.");
        }
    }

}