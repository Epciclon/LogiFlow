-- ================================================
-- LogiFlow - Billing Service - Datos de Prueba
-- Base de datos: db_logiflow_billing
-- Puerto PostgreSQL: 5437
-- ================================================

-- ================================================
-- FACTURAS DE PRUEBA
-- ================================================

-- Factura 1: Urbana pagada
INSERT INTO factura (
    id, numero_factura, pedido_id, numero_pedido,
    cliente_id, cliente_nombre,
    tipo_entrega, distancia_km, peso_kg,
    tarifa_base, cargo_distancia, cargo_peso, recargo_prioridad, descuento,
    subtotal, impuesto_iva, total,
    estado, fecha_emision, fecha_vencimiento, fecha_pago, metodo_pago,
    observaciones, activo, fecha_creacion, fecha_actualizacion
) VALUES (
    gen_random_uuid(),
    'FAC-20240114-150000-0001',
    gen_random_uuid(), 'PED-20240114-150000-0005',
    'c1111111-1111-1111-1111-111111111111', 'Juan Pérez',
    'URBANA_RAPIDA', 3.5, 0.3,
    3.00, 1.75, 0.06, 1.00, 0.00,
    5.81, 0.87, 6.68,
    'PAGADA', '2024-01-14', '2024-01-29', '2024-01-15', 'Tarjeta de Crédito',
    'Pago realizado exitosamente', true, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Factura 2: Intermunicipal pagada
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20240114-090000-0002',
    gen_random_uuid(), 'PED-20240114-090000-0006',
    'c2222222-2222-2222-2222-222222222222', 'María González',
    'INTERMUNICIPAL', 75.0, 15.0,
    10.00, 60.00, 4.50, 1.50, 2.00,
    74.00, 11.10, 85.10,
    'PAGADA', '2024-01-14', '2024-01-29', '2024-01-16', 'Transferencia Bancaria',
    'Pago confirmado', true, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Factura 3: Urbana pendiente
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20240115-100000-0003',
    gen_random_uuid(), 'PED-20240115-100000-0001',
    'c1111111-1111-1111-1111-111111111111', 'Juan Pérez',
    'URBANA_RAPIDA', 5.0, 0.5,
    3.00, 2.50, 0.10, 1.00, 0.00,
    6.60, 0.99, 7.59,
    'PENDIENTE', '2024-01-15', '2024-01-30', NULL, NULL,
    'Factura generada automáticamente', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Factura 4: Intermunicipal borrador
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20240115-080000-0004',
    gen_random_uuid(), 'PED-20240115-080000-0003',
    'c2222222-2222-2222-2222-222222222222', 'María González',
    'INTERMUNICIPAL', 95.0, 8.0,
    10.00, 76.00, 2.40, 1.00, 5.00,
    84.40, 12.66, 97.06,
    'BORRADOR', '2024-01-15', '2024-01-30', NULL, NULL,
    'En revisión', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Factura 5: Nacional pendiente
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20240115-073000-0005',
    gen_random_uuid(), 'PED-20240115-073000-0004',
    'c3333333-3333-3333-3333-333333333333', 'Carlos Sánchez',
    'NACIONAL', 300.0, 100.0,
    50.00, 360.00, 50.00, 2.00, 0.00,
    462.00, 69.30, 531.30,
    'PENDIENTE', '2024-01-15', '2024-01-30', NULL, NULL,
    'Factura de envío nacional', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Factura 6: Nacional pendiente urgente
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20240115-140000-0006',
    gen_random_uuid(), 'PED-20240115-140000-0008',
    'c5555555-5555-5555-5555-555555555555', 'Pedro Ramírez',
    'NACIONAL', 380.0, 45.0,
    50.00, 456.00, 22.50, 2.00, 10.00,
    520.50, 78.08, 598.58,
    'PENDIENTE', '2024-01-15', '2024-01-30', NULL, NULL,
    'Envío urgente de equipos médicos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Factura 7: Urbana vencida
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20231220-100000-0007',
    gen_random_uuid(), 'PED-20231220-100000-0999',
    'c4444444-4444-4444-4444-444444444444', 'Laura Torres',
    'URBANA_RAPIDA', 12.0, 3.0,
    3.00, 6.00, 0.60, 1.00, 0.00,
    10.60, 1.59, 12.19,
    'VENCIDA', '2023-12-20', '2024-01-05', NULL, NULL,
    'Factura vencida - pendiente de cobro', true, CURRENT_TIMESTAMP - INTERVAL '26 days', CURRENT_TIMESTAMP - INTERVAL '10 days'
);

-- Factura 8: Urbana cancelada
INSERT INTO factura VALUES (
    gen_random_uuid(),
    'FAC-20240115-120000-0008',
    gen_random_uuid(), 'PED-20240115-120000-0007',
    'c4444444-4444-4444-4444-444444444444', 'Laura Torres',
    'URBANA_RAPIDA', 8.0, 1.0,
    3.00, 4.00, 0.20, 1.00, 0.00,
    8.20, 1.23, 9.43,
    'CANCELADA', '2024-01-15', '2024-01-30', NULL, NULL,
    'Pedido cancelado por cliente', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- ================================================
-- VERIFICACIÓN
-- ================================================

SELECT 'Facturas por estado' as consulta;
SELECT estado, COUNT(*) as cantidad FROM factura WHERE activo = true GROUP BY estado ORDER BY estado;

SELECT 'Facturas por tipo de entrega' as consulta;
SELECT tipo_entrega, COUNT(*) as cantidad FROM factura WHERE activo = true GROUP BY tipo_entrega ORDER BY tipo_entrega;

SELECT 'Total facturado por estado' as consulta;
SELECT estado, SUM(total) as total_facturado FROM factura WHERE activo = true GROUP BY estado ORDER BY estado;

SELECT 'Resumen general' as consulta;
SELECT 
    COUNT(*) as total_facturas,
    SUM(CASE WHEN estado = 'PAGADA' THEN 1 ELSE 0 END) as pagadas,
    SUM(CASE WHEN estado = 'PENDIENTE' THEN 1 ELSE 0 END) as pendientes,
    SUM(CASE WHEN estado = 'VENCIDA' THEN 1 ELSE 0 END) as vencidas,
    SUM(total) as total_general,
    SUM(CASE WHEN estado = 'PAGADA' THEN total ELSE 0 END) as total_cobrado,
    SUM(CASE WHEN estado = 'PENDIENTE' THEN total ELSE 0 END) as total_por_cobrar
FROM factura WHERE activo = true;

SELECT 'Listado de facturas' as consulta;
SELECT 
    numero_factura,
    cliente_nombre,
    tipo_entrega,
    total,
    estado,
    fecha_emision,
    fecha_vencimiento
FROM factura 
WHERE activo = true
ORDER BY fecha_creacion DESC;

-- ================================================
-- NOTAS
-- ================================================
-- Estados:
-- - 2 PAGADA (cobradas)
-- - 3 PENDIENTE (por cobrar)
-- - 1 BORRADOR (en revisión)
-- - 1 VENCIDA (mora)
-- - 1 CANCELADA (anulada)

-- Tipos de entrega:
-- - 4 URBANA_RAPIDA
-- - 2 INTERMUNICIPAL
-- - 2 NACIONAL

-- Total facturado: ~$967.13
-- Total cobrado: ~$91.78
-- Total por cobrar: ~$734.53
