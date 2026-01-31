# LogiFlow - Plataforma Integral de Gestión de Operaciones para Empresa de Delivery Multinivel

## Descripción General
LogiFlow es una plataforma completa de microservicios diseñada para gestionar operaciones de delivery con 3 niveles de cobertura:
- **Urbana Rápida**: ≤ 20 km
- **Intermunicipal**: ≤ 150 km  
- **Nacional**: Sin límite de distancia

## Arquitectura de Microservicios

```
┌─────────────────────────────────────────────────────────────┐
│                    KONG API GATEWAY                         │
│                   http://localhost:8000                     │
└────────┬─────────────┬─────────────┬────────────┬──────────┘
         │             │             │            │
    ┌────▼────┐  ┌────▼────┐  ┌─────▼────┐  ┌───▼──────┐
    │  AUTH   │  │ PEDIDOS │  │  FLEET   │  │ BILLING  │
    │  :8082  │  │  :8083  │  │  :8084   │  │  :8085   │
    └────┬────┘  └────┬────┘  └─────┬────┘  └───┬──────┘
         │            │             │            │
    ┌────▼────┐  ┌───▼─────┐  ┌────▼────┐  ┌───▼──────┐
    │PostgreSQL│ │PostgreSQL│ │PostgreSQL│ │PostgreSQL│
    │  :5434  │  │  :5435  │  │  :5436   │  │  :5437   │
    └─────────┘  └──────────┘  └──────────┘  └──────────┘
```

## Microservicios

### 1. Auth Service (Puerto 8082)
**Propósito**: Autenticación y autorización con JWT

**Características**:
- 5 roles: CLIENTE, REPARTIDOR, SUPERVISOR, GERENTE, ADMINISTRADOR
- JWT tokens: 24h access, 7d refresh
- BCrypt password hashing
- Bloqueo después de 3 intentos fallidos

**Endpoints principales**:
- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Renovar token

### 2. Pedido Service (Puerto 8083)
**Propósito**: Gestión del ciclo de vida de pedidos

**Características**:
- Validación geográfica con fórmula Haversine
- 3 tipos de entrega: URBANA_RAPIDA, INTERMUNICIPAL, NACIONAL
- 7 estados: PENDIENTE → EN_PREPARACION → ASIGNADO → EN_CAMINO → EN_ENTREGA → ENTREGADO / CANCELADO
- 4 prioridades: URGENTE, ALTA, NORMAL, BAJA
- Número único: PED-YYYYMMDD-HHMMSS-XXXX

**Endpoints principales**:
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos` - Listar pedidos
- `PATCH /api/pedidos/{id}/estado` - Cambiar estado
- `PATCH /api/pedidos/{id}/asignar` - Asignar repartidor

### 3. Fleet Service (Puerto 8084)
**Propósito**: Gestión de flota y personal de reparto

**Características**:
- **Repartidores**: Gestión de licencias, estadísticas, asignación de vehículos
- **Vehículos**: 4 tipos (MOTOCICLETA, AUTOMOVIL, FURGONETA, CAMION)
- Control de documentos: seguro, matrícula, revisión técnica
- Estados: DISPONIBLE, EN_RUTA, DESCANSO, MANTENIMIENTO, INACTIVO

**Endpoints principales**:
- `POST /api/repartidores` - Crear repartidor
- `GET /api/repartidores/disponibles` - Listar disponibles
- `POST /api/vehiculos` - Crear vehículo
- `GET /api/vehiculos/tipo/{tipo}` - Por tipo

### 4. Billing Service (Puerto 8085)
**Propósito**: Facturación dinámica con tarifas configurables

**Características**:
- **Tarifas variables** según tipo de entrega:
  - Urbana: $3.00 + $0.50/km + $0.20/kg
  - Intermunicipal: $10.00 + $0.80/km + $0.30/kg
  - Nacional: $50.00 + $1.20/km + $0.50/kg
- **Recargos** por prioridad: URGENTE x2.0, ALTA x1.5
- **IVA automático**: 15%
- 6 estados: BORRADOR, PENDIENTE, PAGADA, VENCIDA, CANCELADA, ANULADA

**Endpoints principales**:
- `POST /api/facturas` - Crear factura
- `GET /api/facturas/cliente/{id}` - Por cliente
- `PATCH /api/facturas/{id}/pagar` - Registrar pago

## Tecnologías

| Componente | Tecnología | Versión |
|------------|------------|---------|
| Framework | Spring Boot | 4.0.0 |
| Lenguaje | Java | 21 |
| Base de Datos | PostgreSQL | 16 |
| ORM | Spring Data JPA | (Hibernate 7.1.8) |
| Seguridad | Spring Security + JWT | jjwt 0.12.6 |
| Build Tool | Maven | 3.9+ |
| API Gateway | Kong | 3.4 |
| Containerización | Docker + Docker Compose | Latest |
| Documentación API | Springdoc OpenAPI | 2.6.0 |
| Testing | JUnit 5 + Mockito | Latest |

## Documentación OpenAPI (Swagger UI)

Cada microservicio expone su contrato OpenAPI 3.0 y Swagger UI interactivo:

| Servicio | Swagger UI | API Docs JSON |
|----------|------------|---------------|
| Auth Service | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |
| Pedido Service | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |
| Fleet Service | http://localhost:8084/swagger-ui.html | http://localhost:8084/v3/api-docs |
| Billing Service | http://localhost:8085/swagger-ui.html | http://localhost:8085/v3/api-docs |

**A través de Kong Gateway**:
- Auth: http://localhost:8000/auth/swagger-ui.html
- Pedidos: http://localhost:8000/pedidos/swagger-ui.html
- Fleet: http://localhost:8000/fleet/swagger-ui.html
- Billing: http://localhost:8000/billing/swagger-ui.html

## Pruebas Unitarias

Tests JUnit 5 + Mockito implementados en cada servicio:

```powershell
# Ejecutar tests de un servicio específico
cd backend/auth-service
mvn test

# Ejecutar todos los tests
cd delivery
mvn test -pl backend/auth-service,backend/pedido-service,backend/fleet-service,backend/billing-service

# Ver reporte de cobertura
mvn test jacoco:report
```

**Casos de prueba implementados**:
- ✅ Creación de pedido con validación de tipo de entrega
- ✅ Asignación de repartidor disponible  
- ✅ Rechazo de petición no autenticada (401)
- ✅ Rechazo sin permisos (403)
- ✅ Login con credenciales inválidas
- ✅ Pago de factura ya pagada (409)
- ✅ Validación de datos con Jakarta Validation

## Inicio Rápido

### Prerequisitos
- Docker Desktop
- Java 21
- Maven 3.9+
- PowerShell (Windows)

### 1. Levantar la plataforma completa

```powershell
# Clonar el repositorio y navegar al directorio
cd delivery

# Levantar todos los servicios con Docker Compose
docker-compose up -d

# Verificar que todos los servicios estén corriendo
docker-compose ps
```

Espera a que todos los servicios estén en estado "healthy" (~2-3 minutos).

### 2. Configurar Kong Gateway

```powershell
# Ejecutar script de configuración automática
./kong-config.ps1
```

Este script:
- Registra los 4 microservicios en Kong
- Crea rutas de acceso
- Configura CORS

### 3. Probar los endpoints

**A través de Kong (Recomendado)**:
```powershell
# Registro de usuario
curl -X POST http://localhost:8000/auth/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "username": "testuser",
    "email": "test@logiflow.com",
    "password": "password123",
    "nombreCompleto": "Usuario Test"
  }'

# Login
curl -X POST http://localhost:8000/auth/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "username": "admin",
    "password": "password123"
  }'

# Listar pedidos
curl http://localhost:8000/pedidos/api/pedidos

# Listar repartidores
curl http://localhost:8000/fleet/api/repartidores

# Listar facturas
curl http://localhost:8000/billing/api/facturas
```

**Acceso directo a servicios** (sin Kong):
```powershell
curl http://localhost:8082/  # Auth Service
curl http://localhost:8083/  # Pedido Service
curl http://localhost:8084/  # Fleet Service
curl http://localhost:8085/  # Billing Service
```

## Estructura del Proyecto

```
delivery/
├── backend/
│   ├── auth-service/          # Autenticación JWT
│   │   ├── src/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   ├── README.md
│   │   ├── POSTMAN_EXAMPLES.txt
│   │   └── test-data.sql
│   │
│   ├── pedido-service/        # Gestión de pedidos
│   │   ├── src/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   ├── README.md
│   │   ├── POSTMAN_EXAMPLES.txt
│   │   └── test-data.sql
│   │
│   ├── fleet-service/         # Gestión de flota
│   │   ├── src/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   ├── README.md
│   │   └── test-data.sql
│   │
│   └── billing-service/       # Facturación
│       ├── src/
│       ├── Dockerfile
│       ├── pom.xml
│       ├── README.md
│       └── test-data.sql
│
├── docker-compose.yml         # Orquestación completa
├── kong-config.ps1            # Script de configuración Kong
├── KONG_SETUP.md              # Guía detallada de Kong
└── README.md                  # Este archivo
```

## Cargar Datos de Prueba

Cada servicio incluye un archivo `test-data.sql` con datos iniciales:

```powershell
# Auth Service (7 usuarios con diferentes roles)
docker exec -i logiflow_auth_db psql -U postgres -d db_logiflow_auth < backend/auth-service/test-data.sql

# Pedido Service (8 pedidos en diferentes estados)
docker exec -i logiflow_pedidos_db psql -U postgres -d db_logiflow_pedidos < backend/pedido-service/test-data.sql

# Fleet Service (6 vehículos, 7 repartidores)
docker exec -i logiflow_fleet_db psql -U postgres -d db_logiflow_fleet < backend/fleet-service/test-data.sql

# Billing Service (8 facturas en diferentes estados)
docker exec -i logiflow_billing_db psql -U postgres -d db_logiflow_billing < backend/billing-service/test-data.sql
```

## Puertos Utilizados

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **Kong Proxy** | 8000 | Punto de entrada principal |
| **Kong Admin API** | 8001 | Gestión de Kong |
| Auth Service | 8082 | Autenticación directa |
| Pedido Service | 8083 | Pedidos directo |
| Fleet Service | 8084 | Flota directo |
| Billing Service | 8085 | Facturación directo |
| PostgreSQL Auth | 5434 | Base de datos auth |
| PostgreSQL Pedidos | 5435 | Base de datos pedidos |
| PostgreSQL Fleet | 5436 | Base de datos fleet |
| PostgreSQL Billing | 5437 | Base de datos billing |
| PostgreSQL Kong | 5438 | Base de datos Kong |

## Usuarios de Prueba

| Username | Password | Rol | Estado |
|----------|----------|-----|--------|
| admin | password123 | ADMINISTRADOR | Activo |
| gerente1 | password123 | GERENTE | Activo |
| supervisor1 | password123 | SUPERVISOR | Activo |
| repartidor1 | password123 | REPARTIDOR | Activo |
| cliente1 | password123 | CLIENTE | Activo |

## Flujo de Trabajo Típico

### 1. Autenticación
```powershell
# Login y obtener token
$response = curl -X POST http://localhost:8000/auth/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username": "admin", "password": "password123"}'

$token = ($response | ConvertFrom-Json).accessToken
```

### 2. Crear Pedido
```powershell
curl -X POST http://localhost:8000/pedidos/api/pedidos `
  -H "Authorization: Bearer $token" `
  -H "Content-Type: application/json" `
  -d '{...}'
```

### 3. Asignar Repartidor
```powershell
curl -X PATCH http://localhost:8000/pedidos/api/pedidos/{id}/asignar `
  -H "Authorization: Bearer $token" `
  -d '{"repartidorId": "uuid"}'
```

### 4. Generar Factura
```powershell
curl -X POST http://localhost:8000/billing/api/facturas `
  -H "Authorization: Bearer $token" `
  -d '{...}'
```

## Comandos Útiles

### Docker
```powershell
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f auth-service

# Reiniciar un servicio
docker-compose restart auth-service

# Detener todo
docker-compose down

# Detener y eliminar volúmenes (limpieza completa)
docker-compose down -v
```

### Base de Datos
```powershell
# Conectar a PostgreSQL de auth
docker exec -it logiflow_auth_db psql -U postgres -d db_logiflow_auth

# Ver tablas
\dt

# Ver usuarios
SELECT * FROM usuario;
```

### Kong
```powershell
# Ver servicios configurados
curl http://localhost:8001/services

# Ver rutas configuradas
curl http://localhost:8001/routes

# Ver salud de Kong
curl http://localhost:8001/status
```

## Solución de Problemas

### Error del IDE: "declared package does not match expected package"
Este es un **error cosmético del IDE** (IntelliJ/Eclipse), no afecta la compilación:

**Solución 1 - Reiniciar IDE**:
```
File → Invalidate Caches and Restart
```

**Solución 2 - Reconfigurar proyecto**:
1. Click derecho en el proyecto → Maven → Reload Project
2. Build → Rebuild Project

**Solución 3 - Ignorar**:
El código compila correctamente con Maven. El error es solo visual.

### Servicios no inician
```powershell
# Verificar logs
docker-compose logs [nombre-servicio]

# Reiniciar servicios
docker-compose restart

# Reconstruir imágenes
docker-compose up -d --build
```

### Kong no puede conectar a servicios
```powershell
# Verificar red de Docker
docker network inspect delivery_logiflow-network

# Verificar que todos los servicios estén en la misma red
docker-compose ps
```

## Próximos Pasos

- [ ] Implementar autenticación JWT en Kong
- [ ] Agregar rate limiting por usuario
- [ ] Implementar circuit breaker con Resilience4j
- [ ] Agregar monitoring con Prometheus + Grafana
- [ ] Implementar distributed tracing con Zipkin
- [ ] Crear frontend React/Angular
- [ ] Tests unitarios y de integración
- [ ] CI/CD con GitHub Actions
- [ ] Documentación OpenAPI 3.0
- [ ] Health checks personalizados

## Documentación Adicional

- [Auth Service README](backend/auth-service/README.md)
- [Pedido Service README](backend/pedido-service/README.md)
- [Fleet Service README](backend/fleet-service/README.md)
- [Billing Service README](backend/billing-service/README.md)
- [Kong Setup Guide](KONG_SETUP.md)

## Licencia
MIT License - LogiFlow Development Team 2024

---

**¿Necesitas ayuda?** Revisa los logs con `docker-compose logs -f` o consulta la documentación de cada servicio.
