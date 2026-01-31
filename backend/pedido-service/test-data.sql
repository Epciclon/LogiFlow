-- ================================================
-- LogiFlow - Pedido Service - Datos de Prueba
-- Base de datos: db_logiflow_pedidos
-- Puerto PostgreSQL: 5435
-- ================================================

-- Nota: Ejecutar después de que Hibernate cree las tablas

-- ================================================
-- 1. PEDIDOS DE PRUEBA
-- ================================================

-- Pedido 1: Urbano Normal - Recibido
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240115-100000-0001',
    'c1111111-1111-1111-1111-111111111111', --cliente1 de auth-service
    'Juan Pérez',
    'URBANA_RAPIDA', 'RECIBIDO', 'NORMAL',
    'Av. 6 de Diciembre N36-109, Quito',
    -0.1807, -78.4678,
    'Av. Naciones Unidas E10-13, Quito',
    -0.1900, -78.4800,
    'Documentos urgentes',
    0.5, '30x20x5 cm',
    3.50, 4.00,
    CURRENT_TIMESTAMP + INTERVAL '2 hours',
    'Llamar al llegar',
    true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Pedido 2: Urbano Alta - Asignado
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    repartidor_id, repartidor_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240115-103000-0002',
    'c1111111-1111-1111-1111-111111111111',
    'Juan Pérez',
    'r1111111-1111-1111-1111-111111111111', --repartidor de fleet-service
    'Carlos Rodríguez',
    'URBANA_RAPIDA', 'ASIGNADO', 'ALTA',
    'Centro Comercial El Jardín, Quito',
    -0.1650, -78.4850,
    'Mall El Recreo, Quito',
    -0.2540, -78.5250,
    'Paquete mediano - Ropa',
    2.5, '40x30x20 cm',
    4.50, 5.50,
    CURRENT_TIMESTAMP + INTERVAL '3 hours',
    'Empacar con cuidado',
    true,
    CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    CURRENT_TIMESTAMP - INTERVAL '15 minutes'
);

-- Pedido 3: Intermunicipal Normal - En Ruta
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    repartidor_id, repartidor_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240115-080000-0003',
    'c2222222-2222-2222-2222-222222222222', --otro cliente
    'María González',
    'r1111111-1111-1111-1111-111111111111',
    'Carlos Rodríguez',
    'INTERMUNICIPAL', 'EN_RUTA', 'NORMAL',
    'Centro Histórico, Quito',
    -0.2201, -78.5123,
    'Plaza Central, Ambato',
    -1.2490, -78.6167,
    'Productos electrónicos',
    8.0, '50x40x30 cm',
    15.00, 18.00,
    CURRENT_TIMESTAMP + INTERVAL '6 hours',
    'Frágil - Manejar con cuidado',
    true,
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '1 hour'
);

-- Pedido 4: Nacional Urgente - En Preparación
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240115-073000-0004',
    'c3333333-3333-3333-3333-333333333333',
    'Carlos Sánchez',
    'NACIONAL', 'EN_PREPARACION', 'URGENTE',
    'Parque Industrial, Quito',
    -0.2500, -78.5200,
    'Zona Industrial, Guayaquil',
    -2.1700, -79.9224,
    'Carga de maquinaria',
    100.0, '150x100x80 cm',
    80.00, 95.00,
    CURRENT_TIMESTAMP + INTERVAL '24 hours',
    'Entrega en horario de oficina - Requiere montacarga',
    true,
    CURRENT_TIMESTAMP - INTERVAL '3 hours',
    CURRENT_TIMESTAMP - INTERVAL '2 hours'
);

-- Pedido 5: Urbano Normal - Entregado
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    repartidor_id, repartidor_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega, fecha_entrega_real,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240114-150000-0005',
    'c1111111-1111-1111-1111-111111111111',
    'Juan Pérez',
    'r1111111-1111-1111-1111-111111111111',
    'Carlos Rodríguez',
    'URBANA_RAPIDA', 'ENTREGADO', 'NORMAL',
    'Av. Amazonas y Naciones Unidas, Quito',
    -0.1780, -78.4810,
    'Av. Eloy Alfaro N50-234, Quito',
    -0.1650, -78.4900,
    'Documentos legales',
    0.3, '25x20x3 cm',
    3.00, 3.50,
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    'Entrega exitosa - Firmado por secretaria',
    true,
    CURRENT_TIMESTAMP - INTERVAL '5 hours',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
);

-- Pedido 6: Intermunicipal Alta - Entregado
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    repartidor_id, repartidor_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega, fecha_entrega_real,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240114-090000-0006',
    'c2222222-2222-2222-2222-222222222222',
    'María González',
    'r2222222-2222-2222-2222-222222222222',
    'Ana Martínez',
    'INTERMUNICIPAL', 'ENTREGADO', 'ALTA',
    'Terminal Terrestre Quitumbe, Quito',
    -0.2950, -78.5500,
    'Centro Comercial, Latacunga',
    -0.9346, -78.6159,
    'Mercadería variada',
    15.0, '60x50x40 cm',
    20.00, 24.00,
    CURRENT_TIMESTAMP - INTERVAL '8 hours',
    CURRENT_TIMESTAMP - INTERVAL '4 hours',
    'Entrega completada - Cliente satisfecho',
    true,
    CURRENT_TIMESTAMP - INTERVAL '12 hours',
    CURRENT_TIMESTAMP - INTERVAL '4 hours'
);

-- Pedido 7: Urbano Baja - Cancelado
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240115-120000-0007',
    'c4444444-4444-4444-4444-444444444444',
    'Laura Torres',
    'URBANA_RAPIDA', 'CANCELADO', 'BAJA',
    'Parque La Carolina, Quito',
    -0.1752, -78.4840,
    'Parque El Ejido, Quito',
    -0.2010, -78.4950,
    'Paquete pequeño',
    1.0, '20x15x10 cm',
    2.50, 3.00,
    CURRENT_TIMESTAMP + INTERVAL '4 hours',
    'CANCELADO: Cliente no disponible - Ya no necesita el servicio',
    true,
    CURRENT_TIMESTAMP - INTERVAL '1 hour',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
);

-- Pedido 8: Nacional Normal - Recibido
INSERT INTO pedido (
    id, numero_pedido, cliente_id, cliente_nombre,
    tipo_entrega, estado, prioridad,
    direccion_origen, latitud_origen, longitud_origen,
    direccion_destino, latitud_destino, longitud_destino,
    descripcion_paquete, peso_kg, dimensiones,
    tarifa_base, tarifa_total,
    fecha_estimada_entrega,
    observaciones, activo,
    fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'PED-20240115-140000-0008',
    'c5555555-5555-5555-5555-555555555555',
    'Pedro Ramírez',
    'NACIONAL', 'RECIBIDO', 'NORMAL',
    'Aeropuerto Mariscal Sucre, Quito',
    -0.1275, -78.3575,
    'Puerto de Manta',
    -0.9537, -80.7089,
    'Equipos médicos',
    45.0, '100x70x50 cm',
    65.00, 75.00,
    CURRENT_TIMESTAMP + INTERVAL '36 hours',
    'Requiere manejo especial - Equipo delicado',
    true,
    CURRENT_TIMESTAMP - INTERVAL '15 minutes',
    CURRENT_TIMESTAMP - INTERVAL '15 minutes'
);

-- ================================================
-- VERIFICACIÓN DE DATOS
-- ================================================

-- Contar pedidos por estado
SELECT estado, COUNT(*) as cantidad
FROM pedido
WHERE activo = true
GROUP BY estado
ORDER BY estado;

-- Contar pedidos por tipo de entrega
SELECT tipo_entrega, COUNT(*) as cantidad
FROM pedido
WHERE activo = true
GROUP BY tipo_entrega
ORDER BY tipo_entrega;

-- Contar pedidos por prioridad
SELECT prioridad, COUNT(*) as cantidad
FROM pedido
WHERE activo = true
GROUP BY prioridad
ORDER BY 
    CASE prioridad
        WHEN 'URGENTE' THEN 1
        WHEN 'ALTA' THEN 2
        WHEN 'NORMAL' THEN 3
        WHEN 'BAJA' THEN 4
    END;

-- Listar todos los pedidos
SELECT 
    numero_pedido,
    cliente_nombre,
    repartidor_nombre,
    tipo_entrega,
    estado,
    prioridad,
    descripcion_paquete,
    tarifa_total,
    activo
FROM pedido
ORDER BY fecha_creacion DESC;

-- ================================================
-- NOTAS
-- ================================================

-- UUIDs de referencia (deben coincidir con datos en auth-service y fleet-service):
-- c1111111-1111-1111-1111-111111111111: cliente1
-- c2222222-2222-2222-2222-222222222222: cliente2
-- c3333333-3333-3333-3333-333333333333: cliente3
-- c4444444-4444-4444-4444-444444444444: cliente4
-- c5555555-5555-5555-5555-555555555555: cliente5
-- r1111111-1111-1111-1111-111111111111: repartidor1 (Carlos Rodríguez)
-- r2222222-2222-2222-2222-222222222222: repartidor2 (Ana Martínez)

-- Estados de pedidos de prueba:
-- 2 RECIBIDO (nuevos)
-- 1 EN_PREPARACION (preparando)
-- 1 ASIGNADO (repartidor asignado)
-- 1 EN_RUTA (en camino)
-- 2 ENTREGADO (completados)
-- 1 CANCELADO (cancelado)

-- Distribución por tipo:
-- 4 URBANA_RAPIDA (< 20 km)
-- 2 INTERMUNICIPAL (< 150 km)
-- 2 NACIONAL (sin límite)
