package com.imprenta.ordenes_service.repository;

import com.imprenta.ordenes_service.dto.OrdenAsignadaDTO;
import com.imprenta.ordenes_service.dto.reportes.GraficaPastelDTO;
import com.imprenta.ordenes_service.dto.reportes.GraficaRadarDTO;
import com.imprenta.ordenes_service.dto.reportes.OrdenCardDTO;
import com.imprenta.ordenes_service.model.Orden;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    Page<Orden> findByIdEstatusActualNotIn(List<Integer> estatusExcluidos, Pageable pageable);

    Page<Orden> findByIdEstatusActualIn(List<Integer> estatusIncluidos, Pageable pageable);

    Page<Orden> findByIdUsuarioDisenadorAndIdEstatusActualNotIn(
            Integer idUsuarioDisenador,
            List<Integer> estatusExcluidos,
            Pageable pageable);

    @Query(value = """
            SELECT
                o.id_orden as idOrden,
                c.nombre as nombreCliente,
                TO_CHAR(o.fecha_creacion, 'Month DD') as fecha,

                -- ESTO SE QUEDA IGUAL (Para la tarjeta pequeña)
                COALESCE(p_principal.descripcion, 'Varios Productos') as productoPrincipal,

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
                COALESCE(TO_CHAR(o.fecha_entrega_formal, 'DD Mon YYYY'), 'Por definir') as fechaEntrega,

                -- >>> NUEVA COLUMNA (La Mochila) <<<
                -- Crea un texto JSON así: [{"cantidad": 10, "descripcion": "Tazas"}, {...}]
                (
                    SELECT json_agg(json_build_object(
                        'cantidad', do2.cantidad,
                        'descripcion', p2.descripcion
                    ))::text
                    FROM detalle_orden do2
                    JOIN productos p2 ON do2.id_producto = p2.id_producto
                    WHERE do2.id_orden = o.id_orden
                ) as detallesJson

            FROM ordenes o
            JOIN clientes c ON o.id_cliente = c.id_cliente
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            JOIN usuarios u ON o.id_usuario = u.id_usuario

            -- Mantenemos este JOIN limitado para sacar el título de la tarjeta rápido
            LEFT JOIN LATERAL (
                SELECT p.descripcion
                FROM detalle_orden do3
                JOIN productos p ON do3.id_producto = p.id_producto
                WHERE do3.id_orden = o.id_orden
                LIMIT 1
            ) p_principal ON true

            ORDER BY o.fecha_creacion DESC
            """, nativeQuery = true)
    List<OrdenCardDTO> obtenerTarjetasMovil();

    @Query(value = """
            SELECT
                o.id_orden as idOrden,
                o.id_usuario_disenador as idDisenador,
                TO_CHAR(o.fecha_entrega_formal, 'DD Mon') as fechaEntrega,
                ce.descripcion as estatus,

                (SELECT p.descripcion FROM detalle_orden do2
                 JOIN productos p ON do2.id_producto = p.id_producto
                 WHERE do2.id_orden = o.id_orden LIMIT 1) as producto

            FROM ordenes o
            JOIN cat_estatus ce ON o.id_estatus_actual = ce.id_estatus
            WHERE o.id_usuario_disenador IS NOT NULL
              AND o.id_estatus_actual IN (3, 4, 7, 8, 10)
            ORDER BY o.fecha_entrega_formal ASC
            """, nativeQuery = true)
    List<OrdenAsignadaDTO> obtenerOrdenesPorDisenador();

    @Query(value = """
                        SELECT
                            CASE
                                WHEN id_estatus_actual = 1 THEN 'Pendientes'
                                WHEN id_estatus_actual = 11 THEN 'Canceladas'
                                ELSE 'Aprobadas'
                            END as categoria,
                            COUNT(*) as cantidad
            FROM ordenes o
                    WHERE
                        (CAST(:fecha AS DATE) IS NULL OR CAST(o.fecha_creacion AS DATE) = CAST(:fecha AS DATE))
                    GROUP BY categoria
                    """, nativeQuery = true)
    List<GraficaPastelDTO> obtenerResumenEstatus(@Param("fecha") LocalDate fecha);

    @Query(value = """
            SELECT
                r.descripcion AS etiqueta,
                COUNT(o.id_orden) AS valor
            FROM ordenes o
            JOIN cat_razones_cancelacion r ON o.id_razon_cancelacion = r.id_razon
            WHERE
                o.id_estatus_actual = 11
                AND
                (CAST(:fecha AS DATE) IS NULL OR CAST(o.fecha_creacion AS DATE) = CAST(:fecha AS DATE))
            GROUP BY r.descripcion
            ORDER BY valor DESC
            """, nativeQuery = true)
    List<GraficaRadarDTO> obtenerRazonesRechazo(@Param("fecha") LocalDate fecha);
}