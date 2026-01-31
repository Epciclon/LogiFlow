# LogiFlow - Fleet Service

## Descripción
Microservicio para la gestión completa de la flota de repartidores y vehículos de LogiFlow. Administra el personal de entregas, asignación de vehículos, seguimiento de estados y mantenimiento de la flota.

## Características Principales

### Funcionalidades
- ✅ Gestión completa de repartidores (CRUD)
- ✅ Gestión de vehículos de la flota (CRUD)
- ✅ Asignación de vehículos a repartidores
- ✅ Control de estados de repartidores y vehículos
- ✅ Validación de licencias de conducir
- ✅ Control de documentación vehicular (seguro, matrícula, revisión técnica)
- ✅ Seguimiento de mantenimiento de vehículos
- ✅ Estadísticas de repartidores (entregas completadas/canceladas, calificación)
- ✅ Consulta de disponibilidad en tiempo real
- ✅ Eliminación lógica de registros

### Estados del Repartidor
- **DISPONIBLE**: Listo para asignaciones
- **EN_RUTA**: Realizando entrega
- **DESCANSO**: En período de descanso
- **MANTENIMIENTO**: Vehículo en mantenimiento
- **INACTIVO**: Temporalmente no disponible

### Estados del Vehículo
- **DISPONIBLE**: Listo para uso
- **EN_USO**: Siendo utilizado
- **MANTENIMIENTO**: En reparación o servicio
- **FUERA_SERVICIO**: No operativo

### Tipos de Vehículo
- **MOTOCICLETA**: Entregas urbanas rápidas (≤20km)
- **AUTOMOVIL**: Vehículo liviano para distancias medianas
- **FURGONETA**: Entregas de mayor volumen
- **CAMION**: Entregas nacionales pesadas

### Tipos de Licencia
- **TIPO_A**: Motocicletas
- **TIPO_B**: Vehículos livianos
- **TIPO_C**: Vehículos pesados (camiones)
- **TIPO_D**: Transporte público
- **TIPO_E**: Transporte especial

## Tecnologías
- **Framework**: Spring Boot 4.0.0
- **Java**: 21
- **Base de Datos**: PostgreSQL 16
- **ORM**: Spring Data JPA (Hibernate 7.1.8)
- **Validación**: Jakarta Validation
- **Build**: Maven 3.9+

## Configuración

### Base de Datos
```yaml
Puerto PostgreSQL: 5436
Base de datos: db_logiflow_fleet
Usuario: postgres
Password: postgres
```

### Puerto del Servicio
```
Puerto: 8084
```

## API Endpoints

### Repartidores

#### Crear Repartidor
```http
POST /api/repartidores
Content-Type: application/json

{
  "codigoEmpleado": "REP001",
  "nombreCompleto": "Carlos Rodríguez",
  "cedula": "1234567890",
  "email": "carlos.rodriguez@logiflow.com",
  "telefono": "0987654321",
  "direccion": "Av. Principal 123",
  "fechaNacimiento": "1990-05-15",
  "fechaContratacion": "2024-01-10",
  "licenciasConducir": ["TIPO_A", "TIPO_B"],
  "numeroLicencia": "LIC-123456",
  "fechaVencimientoLicencia": "2026-05-15"
}
```

#### Listar Repartidores
```http
GET /api/repartidores
```

#### Obtener por ID
```http
GET /api/repartidores/{id}
```

#### Obtener por Código
```http
GET /api/repartidores/codigo/{codigoEmpleado}
```

#### Obtener por Estado
```http
GET /api/repartidores/estado/{estado}
```
Estados: DISPONIBLE, EN_RUTA, DESCANSO, MANTENIMIENTO, INACTIVO

#### Obtener Disponibles
```http
GET /api/repartidores/disponibles
```

#### Actualizar Repartidor
```http
PUT /api/repartidores/{id}
Content-Type: application/json

{
  "telefono": "0999999999",
  "estado": "EN_RUTA",
  "observaciones": "Actualizado"
}
```

#### Cambiar Estado
```http
PATCH /api/repartidores/{id}/estado
Content-Type: application/json

{
  "estado": "DISPONIBLE"
}
```

#### Asignar Vehículo
```http
PATCH /api/repartidores/{repartidorId}/asignar-vehiculo/{vehiculoId}
```

#### Eliminar Repartidor
```http
DELETE /api/repartidores/{id}
```

### Vehículos

#### Crear Vehículo
```http
POST /api/vehiculos
Content-Type: application/json

{
  "placa": "ABC-1234",
  "tipoVehiculo": "MOTOCICLETA",
  "marca": "Honda",
  "modelo": "XR 190",
  "anio": 2023,
  "color": "Rojo",
  "capacidadCargaKg": 50.0,
  "capacidadVolumenM3": 0.5,
  "kilometraje": 5000,
  "numeroPolizaSeguro": "SEG-123456",
  "fechaVencimientoSeguro": "2025-12-31",
  "fechaVencimientoMatricula": "2025-12-31",
  "fechaVencimientoRevisionTecnica": "2025-06-30"
}
```

#### Listar Vehículos
```http
GET /api/vehiculos
```

#### Obtener por ID
```http
GET /api/vehiculos/{id}
```

#### Obtener por Placa
```http
GET /api/vehiculos/placa/{placa}
```

#### Obtener por Tipo
```http
GET /api/vehiculos/tipo/{tipo}
```
Tipos: MOTOCICLETA, AUTOMOVIL, FURGONETA, CAMION

#### Obtener por Estado
```http
GET /api/vehiculos/estado/{estado}
```
Estados: DISPONIBLE, EN_USO, MANTENIMIENTO, FUERA_SERVICIO

#### Obtener Disponibles
```http
GET /api/vehiculos/disponibles
```

#### Actualizar Vehículo
```http
PUT /api/vehiculos/{id}
Content-Type: application/json

{
  "kilometraje": 10000,
  "estado": "MANTENIMIENTO",
  "observaciones": "Mantenimiento preventivo"
}
```

#### Cambiar Estado
```http
PATCH /api/vehiculos/{id}/estado
Content-Type: application/json

{
  "estado": "DISPONIBLE"
}
```

#### Eliminar Vehículo
```http
DELETE /api/vehiculos/{id}
```

## Modelo de Datos

### Tabla: repartidor

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | ID único (PK) |
| codigo_empleado | VARCHAR(20) | Código único del empleado |
| nombre_completo | VARCHAR(200) | Nombre completo |
| cedula | VARCHAR(20) | Cédula de identidad (único) |
| email | VARCHAR(100) | Email (único) |
| telefono | VARCHAR(20) | Teléfono de contacto |
| direccion | VARCHAR(500) | Dirección domiciliaria |
| fecha_nacimiento | DATE | Fecha de nacimiento |
| fecha_contratacion | DATE | Fecha de ingreso |
| numero_licencia | VARCHAR(30) | Número de licencia |
| fecha_vencimiento_licencia | DATE | Vencimiento de licencia |
| estado | VARCHAR(20) | Estado actual |
| vehiculo_id | UUID | Vehículo asignado (FK) |
| entregas_completadas | INTEGER | Contador de entregas exitosas |
| entregas_canceladas | INTEGER | Contador de entregas canceladas |
| calificacion_promedio | DOUBLE | Calificación promedio |
| observaciones | VARCHAR(500) | Notas adicionales |
| activo | BOOLEAN | Estado lógico |
| fecha_creacion | TIMESTAMP | Fecha de creación |
| fecha_actualizacion | TIMESTAMP | Fecha de actualización |

### Tabla: repartidor_licencias

| Campo | Tipo | Descripción |
|-------|------|-------------|
| repartidor_id | UUID | Referencia a repartidor |
| tipo_licencia | VARCHAR(20) | Tipo de licencia |

### Tabla: vehiculo

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | ID único (PK) |
| placa | VARCHAR(10) | Placa única del vehículo |
| tipo_vehiculo | VARCHAR(20) | MOTOCICLETA, AUTOMOVIL, FURGONETA, CAMION |
| marca | VARCHAR(100) | Marca del vehículo |
| modelo | VARCHAR(100) | Modelo del vehículo |
| anio | INTEGER | Año de fabricación |
| color | VARCHAR(50) | Color |
| capacidad_carga_kg | DECIMAL(10,2) | Capacidad de carga |
| capacidad_volumen_m3 | DECIMAL(10,2) | Capacidad de volumen |
| estado | VARCHAR(20) | Estado actual |
| fecha_ultimo_mantenimiento | DATE | Último mantenimiento |
| fecha_proximo_mantenimiento | DATE | Próximo mantenimiento |
| kilometraje | INTEGER | Kilometraje actual |
| numero_poliza_seguro | VARCHAR(50) | Número de póliza |
| fecha_vencimiento_seguro | DATE | Vencimiento seguro |
| fecha_vencimiento_matricula | DATE | Vencimiento matrícula |
| fecha_vencimiento_revision_tecnica | DATE | Vencimiento revisión |
| observaciones | VARCHAR(500) | Notas adicionales |
| activo | BOOLEAN | Estado lógico |
| fecha_creacion | TIMESTAMP | Fecha de creación |
| fecha_actualizacion | TIMESTAMP | Fecha de actualización |

## Validaciones de Negocio

### Repartidor
- Código de empleado, cédula y email deben ser únicos
- Debe tener al menos una licencia de conducir
- La licencia debe estar vigente para estar disponible
- Debe tener vehículo asignado para estar disponible

### Vehículo
- La placa debe ser única
- Todos los documentos (seguro, matrícula, revisión técnica) deben estar vigentes
- El estado DISPONIBLE requiere documentos vigentes
- Alerta de mantenimiento 7 días antes de la fecha programada

## Ejecución

### Prerequisitos
```bash
# Instalar Java 21
# Instalar Maven 3.9+
# Instalar PostgreSQL 16
```

### Crear Base de Datos
```sql
CREATE DATABASE db_logiflow_fleet;
```

### Compilar y Ejecutar
```bash
./mvnw clean package
./mvnw spring-boot:run
```

El servicio estará disponible en: `http://localhost:8084`

## Integración con Otros Servicios

### Referencias Cruzadas
- **auth-service**: Los repartidores pueden tener cuentas de usuario
- **pedido-service**: Asigna repartidores a pedidos mediante UUID
- **billing-service**: Calcula pagos basados en entregas completadas

## Seguridad
- Validación de entrada con Jakarta Validation
- Transacciones ACID con @Transactional
- Eliminación lógica para auditoría
- Validación de documentos vigentes

## Próximas Mejoras
- [ ] Integración con Kong API Gateway
- [ ] Autenticación JWT
- [ ] Geolocalización en tiempo real
- [ ] Notificaciones de vencimiento de documentos
- [ ] Dashboard de métricas por repartidor
- [ ] Tests unitarios con JUnit 5
- [ ] Documentación OpenAPI 3.0

---
**Versión**: 1.0.0  
**Fecha**: Diciembre 2024  
**Equipo**: LogiFlow Development Team
