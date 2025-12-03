package com.imprenta.ordenes_service.repository;

import com.imprenta.ordenes_service.dto.OrdenAsignadaDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenCardDTO;
import com.imprenta.ordenes_service.model.Orden;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    @Query(value = """
            SELECT
                o.id_orden as idOrden,
                c.nombre as nombreCliente,
                TO_CHAR(o.fecha_creacion, 'Month DD') as fecha,
                COALESCE(p.descripcion, 'Varios Productos') as productoPrincipal,

                CASE
                    WHEN ce.id_estatus = 12 THEN 'Completado'
                    WHEN ce.id_estatus = 11 THEN 'Cancelada'
                    ELSE 'En curso'
                END as estatus,
                ce.descripcion as descripcionEstatus,
                ce.clave as claveEstatus,
                TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') as fechaIso,

                u.nombre as nombreEncargado,
                o.monto_total as montoTotal,
                -- Formateamos la fecha de entrega (si es nula, ponemos 'Pendiente')
                COALESCE(TO_CHAR(o.fecha_entrega_formal, 'DD Mon YYYY'), 'Por definir') as fechaEntrega

            FROM ordenes o
            JOIN clientes c ON o.id_cliente = c.id_cliente
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            JOIN usuarios u ON o.id_usuario = u.id_usuario -- JOIN para el Encargado
            LEFT JOIN LATERAL (
                SELECT p.descripcion
                FROM detalle_orden do2
                JOIN productos p ON do2.id_producto = p.id_producto
                WHERE do2.id_orden = o.id_orden
                LIMIT 1
            ) p ON true
            ORDER BY o.fecha_creacion DESC
            """, nativeQuery = true)
    List<OrdenCardDTO> obtenerTarjetasMovil();

    @Query(value = """
            SELECT
                o.id_orden as idOrden,
                o.id_usuario_disenador as idDisenador,
                TO_CHAR(o.fecha_entrega_formal, 'DD Mon') as fechaEntrega,
                ce.descripcion as estatus,

                -- Subquery para traer nombre del producto principal
                (SELECT p.descripcion FROM detalle_orden do2
                 JOIN productos p ON do2.id_producto = p.id_producto
                 WHERE do2.id_orden = o.id_orden LIMIT 1) as producto

            FROM ordenes o
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            WHERE o.id_usuario_disenador IS NOT NULL
              AND o.id_estatus_actual IN (3, 4, 7, 8, 10) -- Solo fases de diseño
            ORDER BY o.fecha_entrega_formal ASC
            """, nativeQuery = true)
    List<OrdenAsignadaDTO> obtenerOrdenesPorDisenador();
}