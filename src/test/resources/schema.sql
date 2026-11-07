DROP TABLE IF EXISTS Detalle_Orden CASCADE;
DROP TABLE IF EXISTS Ordenes CASCADE;

CREATE TABLE Ordenes (
    id_orden SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_usuario_disenador INT,
    id_cliente INT,
    fecha_creacion TIMESTAMP,
    plazo_estimado_dias INT,
    fecha_entrega_formal DATE,
    monto_total DECIMAL(10,2) NOT NULL,
    requiere_factura BOOLEAN NOT NULL,
    id_condicion_pago INT,
    ruta_archivo VARCHAR(255) NOT NULL,
    fecha_subida TIMESTAMP,
    insumos_verificados BOOLEAN,
    id_estatus_actual INT,
    monto_pagado DECIMAL(10,2),
    id_razon_cancelacion INT,
    notas_diseno TEXT,
    fecha_vencimiento TIMESTAMP
);

CREATE TABLE Detalle_Orden (
    id_detalle SERIAL PRIMARY KEY,
    id_orden INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2),
    importe DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_orden FOREIGN KEY(id_orden) REFERENCES Ordenes(id_orden)
);