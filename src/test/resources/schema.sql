--
-- PostgreSQL database dump
--


-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cat_condiciones_pago; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_condiciones_pago (
    id_condicion integer NOT NULL,
    descripcion character varying(255) NOT NULL,
    numero_plazos integer DEFAULT 1 NOT NULL
);


--
-- Name: cat_condiciones_pago_id_condicion_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_condiciones_pago_id_condicion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_condiciones_pago_id_condicion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_condiciones_pago_id_condicion_seq OWNED BY public.cat_condiciones_pago.id_condicion;


--
-- Name: cat_estatus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_estatus (
    id_estatus integer NOT NULL,
    clave character varying(50) NOT NULL,
    descripcion character varying(255) NOT NULL
);


--
-- Name: cat_estatus_id_estatus_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_estatus_id_estatus_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_estatus_id_estatus_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_estatus_id_estatus_seq OWNED BY public.cat_estatus.id_estatus;


--
-- Name: cat_permisos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_permisos (
    id_permiso integer NOT NULL,
    nombre_clave character varying(100) NOT NULL,
    descripcion text
);


--
-- Name: cat_permisos_id_permiso_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_permisos_id_permiso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_permisos_id_permiso_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_permisos_id_permiso_seq OWNED BY public.cat_permisos.id_permiso;


--
-- Name: cat_razones_cancelacion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_razones_cancelacion (
    id_razon integer NOT NULL,
    descripcion character varying(255) NOT NULL
);


--
-- Name: cat_razones_cancelacion_id_razon_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_razones_cancelacion_id_razon_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_razones_cancelacion_id_razon_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_razones_cancelacion_id_razon_seq OWNED BY public.cat_razones_cancelacion.id_razon;


--
-- Name: cat_regimen_fiscal; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_regimen_fiscal (
    id_regimen integer NOT NULL,
    clave character varying(10) NOT NULL,
    descripcion character varying(255) NOT NULL
);


--
-- Name: cat_regimen_fiscal_id_regimen_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_regimen_fiscal_id_regimen_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_regimen_fiscal_id_regimen_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_regimen_fiscal_id_regimen_seq OWNED BY public.cat_regimen_fiscal.id_regimen;


--
-- Name: cat_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_roles (
    id_rol integer NOT NULL,
    descripcion character varying(100) NOT NULL
);


--
-- Name: cat_roles_id_rol_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_roles_id_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_roles_id_rol_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_roles_id_rol_seq OWNED BY public.cat_roles.id_rol;


--
-- Name: cat_solicitud_estatus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_solicitud_estatus (
    id_solicitud_estatus integer NOT NULL,
    clave character varying(50) NOT NULL,
    descripcion character varying(50) NOT NULL
);


--
-- Name: cat_solicitud_estatus_id_solicitud_estatus_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_solicitud_estatus_id_solicitud_estatus_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_solicitud_estatus_id_solicitud_estatus_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_solicitud_estatus_id_solicitud_estatus_seq OWNED BY public.cat_solicitud_estatus.id_solicitud_estatus;


--
-- Name: cat_tipos_movimiento; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_tipos_movimiento (
    id_tipo integer NOT NULL,
    descripcion character varying(50) NOT NULL
);


--
-- Name: cat_tipos_movimiento_id_tipo_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_tipos_movimiento_id_tipo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_tipos_movimiento_id_tipo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_tipos_movimiento_id_tipo_seq OWNED BY public.cat_tipos_movimiento.id_tipo;


--
-- Name: cat_tipos_pago; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_tipos_pago (
    id_tipo_pago integer NOT NULL,
    descripcion character varying(50) NOT NULL
);


--
-- Name: cat_tipos_pago_id_tipo_pago_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_tipos_pago_id_tipo_pago_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_tipos_pago_id_tipo_pago_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_tipos_pago_id_tipo_pago_seq OWNED BY public.cat_tipos_pago.id_tipo_pago;


--
-- Name: cat_uso_cfdi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cat_uso_cfdi (
    id_uso_cfdi integer NOT NULL,
    clave character varying(3),
    descripcion character varying(255) NOT NULL
);


--
-- Name: cat_uso_cfdi_id_uso_cfdi_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cat_uso_cfdi_id_uso_cfdi_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cat_uso_cfdi_id_uso_cfdi_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cat_uso_cfdi_id_uso_cfdi_seq OWNED BY public.cat_uso_cfdi.id_uso_cfdi;


--
-- Name: clientes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clientes (
    id_cliente integer NOT NULL,
    nombre character varying(150) NOT NULL,
    email character varying(100),
    telefono character varying(20),
    rfc character varying(13),
    razon_social character varying(255),
    direccion_fiscal text,
    id_uso_cfdi integer,
    id_regimen_fiscal integer,
    credito_habilitado boolean DEFAULT false,
    estatus_credito character varying(20),
    dias_credito integer,
    limite_credito numeric(12,2)
);


--
-- Name: clientes_id_cliente_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.clientes_id_cliente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: clientes_id_cliente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.clientes_id_cliente_seq OWNED BY public.clientes.id_cliente;


--
-- Name: detalle_orden; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detalle_orden (
    id_detalle integer NOT NULL,
    id_orden integer NOT NULL,
    id_producto integer NOT NULL,
    cantidad integer NOT NULL,
    precio_unitario numeric(10,2) NOT NULL,
    importe numeric(10,2) NOT NULL
);


--
-- Name: detalle_orden_id_detalle_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.detalle_orden_id_detalle_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: detalle_orden_id_detalle_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.detalle_orden_id_detalle_seq OWNED BY public.detalle_orden.id_detalle;


--
-- Name: detalles_solicitud_insumo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detalles_solicitud_insumo (
    id_detalle integer NOT NULL,
    id_solicitud integer NOT NULL,
    id_producto integer,
    cantidad integer NOT NULL
);


--
-- Name: detalles_solicitud_insumo_id_detalle_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.detalles_solicitud_insumo_id_detalle_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: detalles_solicitud_insumo_id_detalle_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.detalles_solicitud_insumo_id_detalle_seq OWNED BY public.detalles_solicitud_insumo.id_detalle;


--
-- Name: documentos_cliente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.documentos_cliente (
    id bigint NOT NULL,
    id_cliente integer NOT NULL,
    tipo_documento character varying(100) NOT NULL,
    nombre_archivo character varying(255) NOT NULL,
    ruta_archivo character varying(500) NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: documentos_cliente_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.documentos_cliente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: documentos_cliente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.documentos_cliente_id_seq OWNED BY public.documentos_cliente.id;


--
-- Name: facturas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.facturas (
    id_factura integer NOT NULL,
    id_cliente integer NOT NULL,
    id_orden integer NOT NULL,
    folio character varying(50) NOT NULL,
    fecha_emision timestamp without time zone NOT NULL,
    monto_total numeric(10,2) NOT NULL,
    ruta_pdf character varying(255),
    ruta_xml character varying(255)
);


--
-- Name: facturas_id_factura_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.facturas_id_factura_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: facturas_id_factura_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.facturas_id_factura_seq OWNED BY public.facturas.id_factura;


--
-- Name: insumos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.insumos (
    id_insumo integer NOT NULL,
    descripcion character varying(255) NOT NULL,
    unidad_medida character varying(20)
);


--
-- Name: insumos_id_insumo_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.insumos_id_insumo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: insumos_id_insumo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.insumos_id_insumo_seq OWNED BY public.insumos.id_insumo;


--
-- Name: movimientos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movimientos (
    id_movimiento integer NOT NULL,
    id_tipo_movimiento integer NOT NULL,
    concepto character varying(255) NOT NULL,
    monto numeric(10,2) NOT NULL,
    fecha_movimiento timestamp without time zone DEFAULT now(),
    id_usuario_registro integer NOT NULL,
    id_orden integer,
    id_solicitud_insumo integer
);


--
-- Name: movimientos_id_movimiento_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.movimientos_id_movimiento_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: movimientos_id_movimiento_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.movimientos_id_movimiento_seq OWNED BY public.movimientos.id_movimiento;


--
-- Name: notificaciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notificaciones (
    id_notificacion integer NOT NULL,
    emisor_id character varying(50) NOT NULL,
    receptor_id character varying(50) NOT NULL,
    categoria character varying(100) NOT NULL,
    titulo character varying(200) NOT NULL,
    mensaje text NOT NULL,
    leida boolean DEFAULT false,
    creada_en timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    leida_en timestamp without time zone
);


--
-- Name: notificaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notificaciones_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notificaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notificaciones_id_seq OWNED BY public.notificaciones.id_notificacion;


--
-- Name: ordenes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ordenes (
    id_orden integer NOT NULL,
    id_usuario integer NOT NULL,
    id_usuario_disenador integer,
    id_estatus_actual integer,
    id_cliente integer,
    fecha_creacion timestamp without time zone,
    plazo_estimado_dias integer,
    fecha_entrega_formal date,
    monto_pagado numeric(10,2) DEFAULT 0.00,
    monto_total numeric(10,2) NOT NULL,
    requiere_factura boolean DEFAULT false NOT NULL,
    id_condicion_pago integer,
    ruta_archivo character varying(255) NOT NULL,
    fecha_subida timestamp without time zone,
    insumos_verificados boolean DEFAULT false,
    id_razon_cancelacion integer,
    notas_diseno text,
    id_tipo_pago integer,
    fecha_vencimiento timestamp without time zone
);


--
-- Name: ordenes_id_orden_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ordenes_id_orden_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ordenes_id_orden_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ordenes_id_orden_seq OWNED BY public.ordenes.id_orden;


--
-- Name: productos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.productos (
    id_producto integer NOT NULL,
    descripcion character varying(255) NOT NULL,
    precio_unitario numeric(10,2),
    precio_paquete numeric(10,2),
    cantidad_paquete integer,
    tiempo_produccion_dias integer,
    formato_tamano character varying(50),
    unidad_venta character varying(50),
    tiraje_minimo integer,
    volumen_descuento_cantidad integer,
    nombre character varying(255)
);


--
-- Name: productos_id_producto_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.productos_id_producto_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: productos_id_producto_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.productos_id_producto_seq OWNED BY public.productos.id_producto;


--
-- Name: rol_permiso; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rol_permiso (
    id_rol integer NOT NULL,
    id_permiso integer NOT NULL
);


--
-- Name: sobrantes_cliente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sobrantes_cliente (
    id_sobrante integer NOT NULL,
    id_cliente integer NOT NULL,
    id_producto integer NOT NULL,
    id_orden_origen integer NOT NULL,
    cantidad_disponible integer NOT NULL,
    fecha_registro timestamp without time zone DEFAULT now()
);


--
-- Name: sobrantes_cliente_id_sobrante_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.sobrantes_cliente_id_sobrante_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sobrantes_cliente_id_sobrante_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.sobrantes_cliente_id_sobrante_seq OWNED BY public.sobrantes_cliente.id_sobrante;


--
-- Name: solicitudes_insumos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.solicitudes_insumos (
    id_solicitud integer NOT NULL,
    id_orden integer,
    id_usuario_solicitante integer NOT NULL,
    id_estatus integer NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    descripcion_solicitud text
);


--
-- Name: solicitudes_insumos_comprobantes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.solicitudes_insumos_comprobantes (
    id_comprobante integer NOT NULL,
    id_solicitud integer NOT NULL,
    ruta_archivo character varying(255) NOT NULL,
    fecha_subida timestamp without time zone
);


--
-- Name: solicitudes_insumos_comprobantes_id_comprobante_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.solicitudes_insumos_comprobantes_id_comprobante_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: solicitudes_insumos_comprobantes_id_comprobante_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.solicitudes_insumos_comprobantes_id_comprobante_seq OWNED BY public.solicitudes_insumos_comprobantes.id_comprobante;


--
-- Name: solicitudes_insumos_id_solicitud_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.solicitudes_insumos_id_solicitud_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: solicitudes_insumos_id_solicitud_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.solicitudes_insumos_id_solicitud_seq OWNED BY public.solicitudes_insumos.id_solicitud;


--
-- Name: trazabilidad; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trazabilidad (
    id_trazabilidad integer NOT NULL,
    id_estatus integer NOT NULL,
    id_orden integer NOT NULL,
    id_usuario integer NOT NULL,
    fecha_actualizacion timestamp without time zone DEFAULT now()
);


--
-- Name: trazabilidad_id_trazabilidad_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.trazabilidad_id_trazabilidad_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: trazabilidad_id_trazabilidad_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.trazabilidad_id_trazabilidad_seq OWNED BY public.trazabilidad.id_trazabilidad;


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuarios (
    id_usuario integer NOT NULL,
    nombre character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    contrasena character varying(255) NOT NULL,
    id_rol integer NOT NULL
);


--
-- Name: usuarios_id_usuario_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuarios_id_usuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuarios_id_usuario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuarios_id_usuario_seq OWNED BY public.usuarios.id_usuario;


--
-- Name: cat_condiciones_pago id_condicion; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_condiciones_pago ALTER COLUMN id_condicion SET DEFAULT nextval('public.cat_condiciones_pago_id_condicion_seq'::regclass);


--
-- Name: cat_estatus id_estatus; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_estatus ALTER COLUMN id_estatus SET DEFAULT nextval('public.cat_estatus_id_estatus_seq'::regclass);


--
-- Name: cat_permisos id_permiso; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_permisos ALTER COLUMN id_permiso SET DEFAULT nextval('public.cat_permisos_id_permiso_seq'::regclass);


--
-- Name: cat_razones_cancelacion id_razon; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_razones_cancelacion ALTER COLUMN id_razon SET DEFAULT nextval('public.cat_razones_cancelacion_id_razon_seq'::regclass);


--
-- Name: cat_regimen_fiscal id_regimen; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_regimen_fiscal ALTER COLUMN id_regimen SET DEFAULT nextval('public.cat_regimen_fiscal_id_regimen_seq'::regclass);


--
-- Name: cat_roles id_rol; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_roles ALTER COLUMN id_rol SET DEFAULT nextval('public.cat_roles_id_rol_seq'::regclass);


--
-- Name: cat_solicitud_estatus id_solicitud_estatus; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_solicitud_estatus ALTER COLUMN id_solicitud_estatus SET DEFAULT nextval('public.cat_solicitud_estatus_id_solicitud_estatus_seq'::regclass);


--
-- Name: cat_tipos_movimiento id_tipo; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_tipos_movimiento ALTER COLUMN id_tipo SET DEFAULT nextval('public.cat_tipos_movimiento_id_tipo_seq'::regclass);


--
-- Name: cat_tipos_pago id_tipo_pago; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_tipos_pago ALTER COLUMN id_tipo_pago SET DEFAULT nextval('public.cat_tipos_pago_id_tipo_pago_seq'::regclass);


--
-- Name: cat_uso_cfdi id_uso_cfdi; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_uso_cfdi ALTER COLUMN id_uso_cfdi SET DEFAULT nextval('public.cat_uso_cfdi_id_uso_cfdi_seq'::regclass);


--
-- Name: clientes id_cliente; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes ALTER COLUMN id_cliente SET DEFAULT nextval('public.clientes_id_cliente_seq'::regclass);


--
-- Name: detalle_orden id_detalle; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalle_orden ALTER COLUMN id_detalle SET DEFAULT nextval('public.detalle_orden_id_detalle_seq'::regclass);


--
-- Name: detalles_solicitud_insumo id_detalle; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_solicitud_insumo ALTER COLUMN id_detalle SET DEFAULT nextval('public.detalles_solicitud_insumo_id_detalle_seq'::regclass);


--
-- Name: documentos_cliente id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.documentos_cliente ALTER COLUMN id SET DEFAULT nextval('public.documentos_cliente_id_seq'::regclass);


--
-- Name: facturas id_factura; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facturas ALTER COLUMN id_factura SET DEFAULT nextval('public.facturas_id_factura_seq'::regclass);


--
-- Name: insumos id_insumo; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.insumos ALTER COLUMN id_insumo SET DEFAULT nextval('public.insumos_id_insumo_seq'::regclass);


--
-- Name: movimientos id_movimiento; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos ALTER COLUMN id_movimiento SET DEFAULT nextval('public.movimientos_id_movimiento_seq'::regclass);


--
-- Name: notificaciones id_notificacion; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificaciones ALTER COLUMN id_notificacion SET DEFAULT nextval('public.notificaciones_id_seq'::regclass);


--
-- Name: ordenes id_orden; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes ALTER COLUMN id_orden SET DEFAULT nextval('public.ordenes_id_orden_seq'::regclass);


--
-- Name: productos id_producto; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.productos ALTER COLUMN id_producto SET DEFAULT nextval('public.productos_id_producto_seq'::regclass);


--
-- Name: sobrantes_cliente id_sobrante; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sobrantes_cliente ALTER COLUMN id_sobrante SET DEFAULT nextval('public.sobrantes_cliente_id_sobrante_seq'::regclass);


--
-- Name: solicitudes_insumos id_solicitud; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos ALTER COLUMN id_solicitud SET DEFAULT nextval('public.solicitudes_insumos_id_solicitud_seq'::regclass);


--
-- Name: solicitudes_insumos_comprobantes id_comprobante; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos_comprobantes ALTER COLUMN id_comprobante SET DEFAULT nextval('public.solicitudes_insumos_comprobantes_id_comprobante_seq'::regclass);


--
-- Name: trazabilidad id_trazabilidad; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trazabilidad ALTER COLUMN id_trazabilidad SET DEFAULT nextval('public.trazabilidad_id_trazabilidad_seq'::regclass);


--
-- Name: usuarios id_usuario; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id_usuario SET DEFAULT nextval('public.usuarios_id_usuario_seq'::regclass);


--
-- Name: cat_condiciones_pago cat_condiciones_pago_descripcion_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_condiciones_pago
    ADD CONSTRAINT cat_condiciones_pago_descripcion_key UNIQUE (descripcion);


--
-- Name: cat_condiciones_pago cat_condiciones_pago_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_condiciones_pago
    ADD CONSTRAINT cat_condiciones_pago_pkey PRIMARY KEY (id_condicion);


--
-- Name: cat_estatus cat_estatus_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_estatus
    ADD CONSTRAINT cat_estatus_pkey PRIMARY KEY (id_estatus);


--
-- Name: cat_permisos cat_permisos_nombre_clave_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_permisos
    ADD CONSTRAINT cat_permisos_nombre_clave_key UNIQUE (nombre_clave);


--
-- Name: cat_permisos cat_permisos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_permisos
    ADD CONSTRAINT cat_permisos_pkey PRIMARY KEY (id_permiso);


--
-- Name: cat_razones_cancelacion cat_razones_cancelacion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_razones_cancelacion
    ADD CONSTRAINT cat_razones_cancelacion_pkey PRIMARY KEY (id_razon);


--
-- Name: cat_regimen_fiscal cat_regimen_fiscal_clave_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_regimen_fiscal
    ADD CONSTRAINT cat_regimen_fiscal_clave_key UNIQUE (clave);


--
-- Name: cat_regimen_fiscal cat_regimen_fiscal_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_regimen_fiscal
    ADD CONSTRAINT cat_regimen_fiscal_pkey PRIMARY KEY (id_regimen);


--
-- Name: cat_roles cat_roles_descripcion_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_roles
    ADD CONSTRAINT cat_roles_descripcion_key UNIQUE (descripcion);


--
-- Name: cat_roles cat_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_roles
    ADD CONSTRAINT cat_roles_pkey PRIMARY KEY (id_rol);


--
-- Name: cat_solicitud_estatus cat_solicitud_estatus_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_solicitud_estatus
    ADD CONSTRAINT cat_solicitud_estatus_pkey PRIMARY KEY (id_solicitud_estatus);


--
-- Name: cat_tipos_movimiento cat_tipos_movimiento_descripcion_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_tipos_movimiento
    ADD CONSTRAINT cat_tipos_movimiento_descripcion_key UNIQUE (descripcion);


--
-- Name: cat_tipos_movimiento cat_tipos_movimiento_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_tipos_movimiento
    ADD CONSTRAINT cat_tipos_movimiento_pkey PRIMARY KEY (id_tipo);


--
-- Name: cat_tipos_pago cat_tipos_pago_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_tipos_pago
    ADD CONSTRAINT cat_tipos_pago_pkey PRIMARY KEY (id_tipo_pago);


--
-- Name: cat_uso_cfdi cat_uso_cfdi_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cat_uso_cfdi
    ADD CONSTRAINT cat_uso_cfdi_pkey PRIMARY KEY (id_uso_cfdi);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id_cliente);


--
-- Name: clientes clientes_rfc_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_rfc_key UNIQUE (rfc);


--
-- Name: detalle_orden detalle_orden_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalle_orden
    ADD CONSTRAINT detalle_orden_pkey PRIMARY KEY (id_detalle);


--
-- Name: detalles_solicitud_insumo detalles_solicitud_insumo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_solicitud_insumo
    ADD CONSTRAINT detalles_solicitud_insumo_pkey PRIMARY KEY (id_detalle);


--
-- Name: documentos_cliente documentos_cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.documentos_cliente
    ADD CONSTRAINT documentos_cliente_pkey PRIMARY KEY (id);


--
-- Name: facturas facturas_folio_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_folio_key UNIQUE (folio);


--
-- Name: facturas facturas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_pkey PRIMARY KEY (id_factura);


--
-- Name: insumos insumos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.insumos
    ADD CONSTRAINT insumos_pkey PRIMARY KEY (id_insumo);


--
-- Name: movimientos movimientos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_pkey PRIMARY KEY (id_movimiento);


--
-- Name: notificaciones notificaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificaciones
    ADD CONSTRAINT notificaciones_pkey PRIMARY KEY (id_notificacion);


--
-- Name: ordenes ordenes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_pkey PRIMARY KEY (id_orden);


--
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (id_producto);


--
-- Name: rol_permiso rol_permiso_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol_permiso
    ADD CONSTRAINT rol_permiso_pkey PRIMARY KEY (id_rol, id_permiso);


--
-- Name: sobrantes_cliente sobrantes_cliente_id_cliente_id_producto_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sobrantes_cliente
    ADD CONSTRAINT sobrantes_cliente_id_cliente_id_producto_key UNIQUE (id_cliente, id_producto);


--
-- Name: sobrantes_cliente sobrantes_cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sobrantes_cliente
    ADD CONSTRAINT sobrantes_cliente_pkey PRIMARY KEY (id_sobrante);


--
-- Name: solicitudes_insumos_comprobantes solicitudes_insumos_comprobantes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos_comprobantes
    ADD CONSTRAINT solicitudes_insumos_comprobantes_pkey PRIMARY KEY (id_comprobante);


--
-- Name: solicitudes_insumos solicitudes_insumos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos
    ADD CONSTRAINT solicitudes_insumos_pkey PRIMARY KEY (id_solicitud);


--
-- Name: trazabilidad trazabilidad_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trazabilidad
    ADD CONSTRAINT trazabilidad_pkey PRIMARY KEY (id_trazabilidad);


--
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id_usuario);


--
-- Name: idx_notificaciones_receptor_leida; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notificaciones_receptor_leida ON public.notificaciones USING btree (receptor_id, leida);


--
-- Name: clientes clientes_id_regimen_fiscal_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_id_regimen_fiscal_fkey FOREIGN KEY (id_regimen_fiscal) REFERENCES public.cat_regimen_fiscal(id_regimen);


--
-- Name: clientes clientes_id_uso_cfdi_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_id_uso_cfdi_fkey FOREIGN KEY (id_uso_cfdi) REFERENCES public.cat_uso_cfdi(id_uso_cfdi);


--
-- Name: detalle_orden detalle_orden_id_orden_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalle_orden
    ADD CONSTRAINT detalle_orden_id_orden_fkey FOREIGN KEY (id_orden) REFERENCES public.ordenes(id_orden);


--
-- Name: detalle_orden detalle_orden_id_producto_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalle_orden
    ADD CONSTRAINT detalle_orden_id_producto_fkey FOREIGN KEY (id_producto) REFERENCES public.productos(id_producto);


--
-- Name: facturas facturas_id_cliente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.clientes(id_cliente);


--
-- Name: facturas facturas_id_orden_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facturas
    ADD CONSTRAINT facturas_id_orden_fkey FOREIGN KEY (id_orden) REFERENCES public.ordenes(id_orden);


--
-- Name: documentos_cliente fk_documentos_cliente; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.documentos_cliente
    ADD CONSTRAINT fk_documentos_cliente FOREIGN KEY (id_cliente) REFERENCES public.clientes(id_cliente) ON DELETE CASCADE;


--
-- Name: ordenes fk_orden_tipo_pago; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT fk_orden_tipo_pago FOREIGN KEY (id_tipo_pago) REFERENCES public.cat_tipos_pago(id_tipo_pago);


--
-- Name: detalles_solicitud_insumo fk_solicitud; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_solicitud_insumo
    ADD CONSTRAINT fk_solicitud FOREIGN KEY (id_solicitud) REFERENCES public.solicitudes_insumos(id_solicitud) ON DELETE CASCADE;


--
-- Name: movimientos movimientos_id_orden_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_id_orden_fkey FOREIGN KEY (id_orden) REFERENCES public.ordenes(id_orden);


--
-- Name: movimientos movimientos_id_solicitud_insumo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_id_solicitud_insumo_fkey FOREIGN KEY (id_solicitud_insumo) REFERENCES public.solicitudes_insumos(id_solicitud);


--
-- Name: movimientos movimientos_id_tipo_movimiento_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_id_tipo_movimiento_fkey FOREIGN KEY (id_tipo_movimiento) REFERENCES public.cat_tipos_movimiento(id_tipo);


--
-- Name: movimientos movimientos_id_usuario_registro_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_id_usuario_registro_fkey FOREIGN KEY (id_usuario_registro) REFERENCES public.usuarios(id_usuario);


--
-- Name: ordenes ordenes_id_cliente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.clientes(id_cliente);


--
-- Name: ordenes ordenes_id_condicion_pago_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_id_condicion_pago_fkey FOREIGN KEY (id_condicion_pago) REFERENCES public.cat_condiciones_pago(id_condicion);


--
-- Name: ordenes ordenes_id_estatus_actual_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_id_estatus_actual_fkey FOREIGN KEY (id_estatus_actual) REFERENCES public.cat_estatus(id_estatus);


--
-- Name: ordenes ordenes_id_razon_cancelacion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_id_razon_cancelacion_fkey FOREIGN KEY (id_razon_cancelacion) REFERENCES public.cat_razones_cancelacion(id_razon);


--
-- Name: ordenes ordenes_id_usuario_disenador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_id_usuario_disenador_fkey FOREIGN KEY (id_usuario_disenador) REFERENCES public.usuarios(id_usuario);


--
-- Name: ordenes ordenes_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ordenes
    ADD CONSTRAINT ordenes_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuarios(id_usuario);


--
-- Name: rol_permiso rol_permiso_id_permiso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol_permiso
    ADD CONSTRAINT rol_permiso_id_permiso_fkey FOREIGN KEY (id_permiso) REFERENCES public.cat_permisos(id_permiso);


--
-- Name: rol_permiso rol_permiso_id_rol_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol_permiso
    ADD CONSTRAINT rol_permiso_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.cat_roles(id_rol);


--
-- Name: sobrantes_cliente sobrantes_cliente_id_cliente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sobrantes_cliente
    ADD CONSTRAINT sobrantes_cliente_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.clientes(id_cliente);


--
-- Name: sobrantes_cliente sobrantes_cliente_id_orden_origen_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sobrantes_cliente
    ADD CONSTRAINT sobrantes_cliente_id_orden_origen_fkey FOREIGN KEY (id_orden_origen) REFERENCES public.ordenes(id_orden);


--
-- Name: sobrantes_cliente sobrantes_cliente_id_producto_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sobrantes_cliente
    ADD CONSTRAINT sobrantes_cliente_id_producto_fkey FOREIGN KEY (id_producto) REFERENCES public.productos(id_producto);


--
-- Name: solicitudes_insumos_comprobantes solicitudes_insumos_comprobantes_id_solicitud_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos_comprobantes
    ADD CONSTRAINT solicitudes_insumos_comprobantes_id_solicitud_fkey FOREIGN KEY (id_solicitud) REFERENCES public.solicitudes_insumos(id_solicitud);


--
-- Name: solicitudes_insumos solicitudes_insumos_id_estatus_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos
    ADD CONSTRAINT solicitudes_insumos_id_estatus_fkey FOREIGN KEY (id_estatus) REFERENCES public.cat_solicitud_estatus(id_solicitud_estatus);


--
-- Name: solicitudes_insumos solicitudes_insumos_id_orden_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos
    ADD CONSTRAINT solicitudes_insumos_id_orden_fkey FOREIGN KEY (id_orden) REFERENCES public.ordenes(id_orden);


--
-- Name: solicitudes_insumos solicitudes_insumos_id_usuario_solicitante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.solicitudes_insumos
    ADD CONSTRAINT solicitudes_insumos_id_usuario_solicitante_fkey FOREIGN KEY (id_usuario_solicitante) REFERENCES public.usuarios(id_usuario);


--
-- Name: trazabilidad trazabilidad_id_estatus_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trazabilidad
    ADD CONSTRAINT trazabilidad_id_estatus_fkey FOREIGN KEY (id_estatus) REFERENCES public.cat_estatus(id_estatus);


--
-- Name: trazabilidad trazabilidad_id_orden_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trazabilidad
    ADD CONSTRAINT trazabilidad_id_orden_fkey FOREIGN KEY (id_orden) REFERENCES public.ordenes(id_orden);


--
-- Name: trazabilidad trazabilidad_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.trazabilidad
    ADD CONSTRAINT trazabilidad_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuarios(id_usuario);


--
-- Name: usuarios usuarios_id_rol_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.cat_roles(id_rol);


--
-- PostgreSQL database dump complete
--


