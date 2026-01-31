-- ================================================
-- LogiFlow - Fleet Service - Datos de Prueba
-- Base de datos: db_logiflow_fleet
-- Puerto PostgreSQL: 5436
-- ================================================

-- ================================================
-- 1. VEHÍCULOS
-- ================================================

-- Vehículo 1: Motocicleta disponible
INSERT INTO vehiculo (
    id, placa, tipo_vehiculo, marca, modelo, anio, color,
    capacidad_carga_kg, capacidad_volumen_m3,
    estado, kilometraje,
    numero_poliza_seguro, fecha_vencimiento_seguro,
    fecha_vencimiento_matricula, fecha_vencimiento_revision_tecnica,
    observaciones, activo, fecha_creacion, fecha_actualizacion
) VALUES (
    'v1111111-1111-1111-1111-111111111111',
    'QUI-1234', 'MOTOCICLETA', 'Honda', 'XR 190', 2023, 'Rojo',
    50.0, 0.5,
    'DISPONIBLE', 5000,
    'SEG-001', '2026-12-31',
    '2026-12-31', '2026-06-30',
    'Vehículo en perfecto estado', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Vehículo 2: Motocicleta en uso
INSERT INTO vehiculo VALUES (
    'v2222222-2222-2222-2222-222222222222',
    'QUI-5678', 'MOTOCICLETA', 'Yamaha', 'FZ 150', 2022, 'Azul',
    45.0, 0.4,
    'EN_USO', 12000,
    'SEG-002', '2026-11-30',
    '2026-11-30', '2026-05-31',
    'En uso actualmente', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Vehículo 3: Automóvil disponible
INSERT INTO vehiculo VALUES (
    'v3333333-3333-3333-3333-333333333333',
    'GYE-9876', 'AUTOMOVIL', 'Chevrolet', 'Sail', 2021, 'Blanco',
    200.0, 2.0,
    'DISPONIBLE', 45000,
    'SEG-003', '2027-01-15',
    '2027-01-15', '2026-07-15',
    'Apto para entregas intermunicipales', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Vehículo 4: Furgoneta disponible
INSERT INTO vehiculo VALUES (
    'v4444444-4444-4444-4444-444444444444',
    'CUE-4567', 'FURGONETA', 'Hyundai', 'H100', 2020, 'Gris',
    800.0, 5.0,
    'DISPONIBLE', 78000,
    'SEG-004', '2026-10-31',
    '2026-10-31', '2026-04-30',
    'Ideal para cargas medianas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Vehículo 5: Camión disponible
INSERT INTO vehiculo VALUES (
    'v5555555-5555-5555-5555-555555555555',
    'LJA-7890', 'CAMION', 'Hino', 'FC 1016', 2019, 'Blanco',
    3000.0, 20.0,
    'DISPONIBLE', 125000,
    'SEG-005', '2026-09-30',
    '2026-09-30', '2026-03-31',
    'Para entregas nacionales pesadas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Vehículo 6: En mantenimiento
INSERT INTO vehiculo VALUES (
    gen_random_uuid(),
    'QUI-3333', 'AUTOMOVIL', 'Nissan', 'Versa', 2018, 'Negro',
    180.0, 1.8,
    'MANTENIMIENTO', 95000,
    'SEG-006', '2026-08-31',
    '2026-08-31', '2026-02-28',
    'Mantenimiento preventivo programado', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- ================================================
-- 2. REPARTIDORES
-- ================================================

-- Repartidor 1: Disponible con motocicleta
INSERT INTO repartidor (
    id, codigo_empleado, nombre_completo, cedula, email, telefono,
    direccion, fecha_nacimiento, fecha_contratacion,
    numero_licencia, fecha_vencimiento_licencia,
    estado, vehiculo_id,
    entregas_completadas, entregas_canceladas, calificacion_promedio,
    observaciones, activo, fecha_creacion, fecha_actualizacion
) VALUES (
    'r1111111-1111-1111-1111-111111111111',
    'REP001', 'Carlos Rodríguez', '1723456789', 'carlos.rodriguez@logiflow.com', '0987654321',
    'Av. 10 de Agosto N25-123, Quito', '1990-05-15', '2024-01-10',
    'LIC-123456', '2026-05-15',
    'DISPONIBLE', 'v1111111-1111-1111-1111-111111111111',
    45, 2, 4.7,
    'Repartidor experimentado', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Licencias del repartidor 1
INSERT INTO repartidor_licencias (repartidor_id, tipo_licencia)
VALUES ('r1111111-1111-1111-1111-111111111111', 'TIPO_A');
INSERT INTO repartidor_licencias (repartidor_id, tipo_licencia)
VALUES ('r1111111-1111-1111-1111-111111111111', 'TIPO_B');

-- Repartidor 2: En ruta con motocicleta
INSERT INTO repartidor VALUES (
    'r2222222-2222-2222-2222-222222222222',
    'REP002', 'Ana Martínez', '1798765432', 'ana.martinez@logiflow.com', '0976543210',
    'Calle García Moreno S3-45, Quito', '1992-08-22', '2024-02-01',
    'LIC-234567', '2027-08-22',
    'EN_RUTA', 'v2222222-2222-2222-2222-222222222222',
    38, 1, 4.9,
    'Excelente puntualidad', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO repartidor_licencias VALUES ('r2222222-2222-2222-2222-222222222222', 'TIPO_A');

-- Repartidor 3: Disponible con automóvil
INSERT INTO repartidor VALUES (
    'r3333333-3333-3333-3333-333333333333',
    'REP003', 'Luis García', '1712345678', 'luis.garcia@logiflow.com', '0965432109',
    'Av. Amazonas N45-67, Quito', '1988-11-30', '2023-11-15',
    'LIC-345678', '2026-11-30',
    'DISPONIBLE', 'v3333333-3333-3333-3333-333333333333',
    62, 3, 4.6,
    'Especialista en rutas intermunicipales', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO repartidor_licencias VALUES ('r3333333-3333-3333-3333-333333333333', 'TIPO_B');
INSERT INTO repartidor_licencias VALUES ('r3333333-3333-3333-3333-333333333333', 'TIPO_C');

-- Repartidor 4: Disponible con furgoneta
INSERT INTO repartidor VALUES (
    'r4444444-4444-4444-4444-444444444444',
    'REP004', 'María González', '1787654321', 'maria.gonzalez@logiflow.com', '0954321098',
    'Av. Eloy Alfaro N50-234, Quito', '1985-03-18', '2023-08-20',
    'LIC-456789', '2027-03-18',
    'DISPONIBLE', 'v4444444-4444-4444-4444-444444444444',
    71, 4, 4.8,
    'Experiencia en cargas voluminosas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO repartidor_licencias VALUES ('r4444444-4444-4444-4444-444444444444', 'TIPO_B');
INSERT INTO repartidor_licencias VALUES ('r4444444-4444-4444-4444-444444444444', 'TIPO_C');

-- Repartidor 5: Disponible con camión
INSERT INTO repartidor VALUES (
    'r5555555-5555-5555-5555-555555555555',
    'REP005', 'Pedro Ramírez', '1701234567', 'pedro.ramirez@logiflow.com', '0943210987',
    'Parque Industrial, Quito', '1980-07-25', '2023-06-01',
    'LIC-567890', '2027-07-25',
    'DISPONIBLE', 'v5555555-5555-5555-5555-555555555555',
    89, 5, 4.5,
    'Conductor profesional de camiones', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO repartidor_licencias VALUES ('r5555555-5555-5555-5555-555555555555', 'TIPO_C');
INSERT INTO repartidor_licencias VALUES ('r5555555-5555-5555-5555-555555555555', 'TIPO_D');

-- Repartidor 6: En descanso
INSERT INTO repartidor VALUES (
    gen_random_uuid(),
    'REP006', 'Sofía Torres', '1734567890', 'sofia.torres@logiflow.com', '0932109876',
    'La Floresta, Quito', '1995-12-10', '2024-03-15',
    'LIC-678901', '2028-12-10',
    'DESCANSO', NULL,
    15, 0, 5.0,
    'Repartidora nueva con excelente desempeño', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO repartidor_licencias VALUES (
    (SELECT id FROM repartidor WHERE codigo_empleado = 'REP006'),
    'TIPO_A'
);

-- Repartidor 7: Inactivo temporalmente
INSERT INTO repartidor VALUES (
    gen_random_uuid(),
    'REP007', 'Diego Mendoza', '1745678901', 'diego.mendoza@logiflow.com', '0921098765',
    'Cumbayá, Quito', '1993-04-05', '2023-09-10',
    'LIC-789012', '2026-04-05',
    'INACTIVO', NULL,
    52, 6, 4.3,
    'Licencia vencida - en proceso de renovación', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO repartidor_licencias VALUES (
    (SELECT id FROM repartidor WHERE codigo_empleado = 'REP007'),
    'TIPO_A'
);
INSERT INTO repartidor_licencias VALUES (
    (SELECT id FROM repartidor WHERE codigo_empleado = 'REP007'),
    'TIPO_B'
);

-- ================================================
-- VERIFICACIÓN
-- ================================================

SELECT 'Vehículos por estado' as consulta;
SELECT estado, COUNT(*) FROM vehiculo WHERE activo = true GROUP BY estado;

SELECT 'Vehículos por tipo' as consulta;
SELECT tipo_vehiculo, COUNT(*) FROM vehiculo WHERE activo = true GROUP BY tipo_vehiculo;

SELECT 'Repartidores por estado' as consulta;
SELECT estado, COUNT(*) FROM repartidor WHERE activo = true GROUP BY estado;

SELECT 'Repartidores con vehículo asignado' as consulta;
SELECT COUNT(*) FROM repartidor WHERE vehiculo_id IS NOT NULL AND activo = true;
