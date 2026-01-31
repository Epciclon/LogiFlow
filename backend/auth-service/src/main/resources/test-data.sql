-- ============================================================
-- DATOS DE PRUEBA - AUTH SERVICE - LogiFlow
-- ============================================================
-- Script para insertar usuarios de prueba con diferentes roles
-- Los passwords están encriptados con BCrypt (password: "password123")
-- ============================================================

-- Limpiar datos existentes
DELETE FROM usuario_roles;
DELETE FROM usuarios;

-- ============================================================
-- USUARIO 1: Cliente
-- username: cliente1, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'cliente1', 'cliente1@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'Juan Pérez Cliente', '0991234567', true, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'CLIENTE' FROM usuarios WHERE username = 'cliente1';

-- ============================================================
-- USUARIO 2: Repartidor
-- username: repartidor1, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'repartidor1', 'repartidor1@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'Carlos Gómez Repartidor', '0987654321', true, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'REPARTIDOR' FROM usuarios WHERE username = 'repartidor1';

-- ============================================================
-- USUARIO 3: Supervisor
-- username: supervisor1, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'supervisor1', 'supervisor1@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'María López Supervisora', '0998765432', true, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'SUPERVISOR' FROM usuarios WHERE username = 'supervisor1';

-- ============================================================
-- USUARIO 4: Gerente
-- username: gerente1, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'gerente1', 'gerente1@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'Roberto Martínez Gerente', '0991111111', true, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'GERENTE' FROM usuarios WHERE username = 'gerente1';

-- ============================================================
-- USUARIO 5: Administrador
-- username: admin, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'admin', 'admin@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'Administrador del Sistema', '0999999999', true, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ADMINISTRADOR' FROM usuarios WHERE username = 'admin';

-- ============================================================
-- USUARIO 6: Usuario con múltiples roles (Supervisor + Repartidor)
-- username: multi1, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'multi1', 'multi1@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'Ana Torres Multi-Rol', '0992222222', true, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'SUPERVISOR' FROM usuarios WHERE username = 'multi1'
UNION ALL
SELECT id, 'REPARTIDOR' FROM usuarios WHERE username = 'multi1';

-- ============================================================
-- USUARIO 7: Usuario inactivo para pruebas
-- username: inactivo1, password: password123
-- ============================================================
INSERT INTO usuarios (id, username, email, password, nombre_completo, telefono, activo, cuenta_bloqueada, intentos_fallidos, fecha_creacion, fecha_actualizacion)
VALUES (gen_random_uuid(), 'inactivo1', 'inactivo1@logiflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/JDYEq1SJQvW6Xn8/DI8kI1tMx0dGlG', 'Usuario Inactivo', '0983333333', false, false, 0, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'CLIENTE' FROM usuarios WHERE username = 'inactivo1';

-- ============================================================
-- CONSULTAS DE VERIFICACIÓN
-- ============================================================

-- Ver todos los usuarios con sus roles
SELECT 
    u.username,
    u.email,
    u.nombre_completo,
    u.activo,
    u.cuenta_bloqueada,
    STRING_AGG(ur.rol, ', ' ORDER BY ur.rol) as roles
FROM usuarios u
LEFT JOIN usuario_roles ur ON u.id = ur.usuario_id
GROUP BY u.id, u.username, u.email, u.nombre_completo, u.activo, u.cuenta_bloqueada
ORDER BY u.username;

-- Contar usuarios por rol
SELECT 
    rol,
    COUNT(*) as total_usuarios
FROM usuario_roles
GROUP BY rol
ORDER BY rol;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
