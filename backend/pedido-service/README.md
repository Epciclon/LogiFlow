# LogiFlow - Pedido Service

## Descripción
Microservicio para la gestión completa del ciclo de vida de pedidos en la plataforma LogiFlow. Maneja la creación, asignación, seguimiento y entrega de pedidos con validación de cobertura geográfica.

## Características Principales

### Funcionalidades
- ✅ Creación de pedidos con validación de cobertura geográfica
- ✅ Generación automática de número de pedido único
- ✅ Gestión de estados del pedido (7 estados)
- ✅ Asignación de repartidores
- ✅ Consulta de pedidos por cliente, repartidor y estado
- ✅ Seguimiento de entregas con coordenadas geográficas
- ✅ Cálculo de distancias usando fórmula de Haversine
- ✅ Gestión de prioridades (BAJA, NORMAL, ALTA, URGENTE)
- ✅ Cancelación de pedidos con motivo
- ✅ Eliminación lógica de registros

### Tipos de Entrega
- **URBANA_RAPIDA**: Entregas hasta 20 km (motocicleta)
- **INTERMUNICIPAL**: Entregas hasta 150 km (vehículo liviano)
- **NACIONAL**: Sin límite de distancia (camión/furgón)

### Estados del Pedido
1. **RECIBIDO**: Pedido creado en el sistema
2. **EN_PREPARACION**: Preparando paquete
3. **ASIGNADO**: Repartidor asignado
4. **EN_RUTA**: Pedido en camino
5. **ENTREGADO**: Entrega completada
6. **CANCELADO**: Pedido cancelado
7. **DEVUELTO**: Devolución procesada

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
Puerto PostgreSQL: 5435
Base de datos: db_logiflow_pedidos
Usuario: postgres
Password: postgres
```

### Puerto del Servicio
```
Puerto: 8083
```

### Variables de Entorno (application.yaml)
```yaml
spring:
  application:
    name: pedido-service
  datasource:
    url: jdbc:postgresql://localhost:5435/db_logiflow_pedidos
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## Estructura del Proyecto

```
pedido-service/
├── src/main/java/ec/edu/espe/pedido_service/
│   ├── controller/
│   │   ├── PedidoController.java       # REST endpoints
│   │   └── HomeController.java         # Info del servicio
│   ├── service/
│   │   └── PedidoService.java          # Lógica de negocio
│   ├── repository/
│   │   └── PedidoRepository.java       # Acceso a datos
│   ├── model/
│   │   ├── Pedido.java                 # Entidad principal
│   │   ├── TipoEntrega.java            # Enum tipos
│   │   ├── EstadoPedido.java           # Enum estados
│   │   └── PrioridadPedido.java        # Enum prioridades
│   ├── dto/
│   │   ├── CreatePedidoRequest.java    # DTO creación
│   │   ├── UpdatePedidoRequest.java    # DTO actualización
│   │   └── PedidoResponse.java         # DTO respuesta
│   └── PedidoServiceApplication.java   # Clase principal
└── src/main/resources/
    └── application.yaml                # Configuración
```

## API Endpoints

### Información del Servicio
```http
GET / HTTP/1.1
```
Respuesta:
```json
{
  "servicio": "LogiFlow - Pedido Service",
  "version": "1.0.0",
  "puerto": 8083,
  "endpoints": {...}
}
```

### Crear Pedido
```http
POST /api/pedidos HTTP/1.1
Content-Type: application/json

{
  "clienteId": "uuid",
  "clienteNombre": "Juan Pérez",
  "tipoEntrega": "URBANA_RAPIDA",
  "prioridad": "NORMAL",
  "direccionOrigen": "Av. Principal 123",
  "latitudOrigen": -0.1807,
  "longitudOrigen": -78.4678,
  "direccionDestino": "Calle Secundaria 456",
  "latitudDestino": -0.1900,
  "longitudDestino": -78.4800,
  "descripcionPaquete": "Documentos urgentes",
  "pesoKg": 0.5,
  "dimensiones": "30x20x5 cm",
  "observaciones": "Llamar al llegar"
}
```

### Listar Todos los Pedidos
```http
GET /api/pedidos HTTP/1.1
```

### Obtener Pedido por ID
```http
GET /api/pedidos/{id} HTTP/1.1
```

### Obtener Pedido por Número
```http
GET /api/pedidos/numero/{numeroPedido} HTTP/1.1
```

### Obtener Pedidos por Cliente
```http
GET /api/pedidos/cliente/{clienteId} HTTP/1.1
```

### Obtener Pedidos por Repartidor
```http
GET /api/pedidos/repartidor/{repartidorId} HTTP/1.1
```

### Obtener Pedidos por Estado
```http
GET /api/pedidos/estado/{estado} HTTP/1.1
```
Estados válidos: RECIBIDO, EN_PREPARACION, ASIGNADO, EN_RUTA, ENTREGADO, CANCELADO, DEVUELTO

### Actualizar Pedido
```http
PUT /api/pedidos/{id} HTTP/1.1
Content-Type: application/json

{
  "estado": "EN_PREPARACION",
  "prioridad": "ALTA",
  "tarifaBase": 5.50,
  "tarifaTotal": 6.50,
  "observaciones": "Actualizado"
}
```

### Asignar Repartidor
```http
PATCH /api/pedidos/{pedidoId}/asignar-repartidor HTTP/1.1
Content-Type: application/json

{
  "repartidorId": "uuid",
  "repartidorNombre": "Carlos Rodríguez"
}
```

### Cambiar Estado
```http
PATCH /api/pedidos/{pedidoId}/estado HTTP/1.1
Content-Type: application/json

{
  "estado": "EN_RUTA"
}
```

### Cancelar Pedido
```http
PATCH /api/pedidos/{pedidoId}/cancelar HTTP/1.1
Content-Type: application/json

{
  "motivo": "Cliente no disponible"
}
```

### Eliminar Pedido (Lógico)
```http
DELETE /api/pedidos/{id} HTTP/1.1
```

## Modelo de Datos

### Tabla: pedido

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | ID único (PK) |
| numero_pedido | VARCHAR(50) | Número único (PED-YYYYMMDD-HHMMSS-XXXX) |
| cliente_id | UUID | Referencia a auth-service |
| cliente_nombre | VARCHAR(200) | Nombre del cliente |
| repartidor_id | UUID | Referencia a fleet-service |
| repartidor_nombre | VARCHAR(200) | Nombre del repartidor |
| tipo_entrega | VARCHAR(20) | URBANA_RAPIDA, INTERMUNICIPAL, NACIONAL |
| estado | VARCHAR(20) | Estado actual del pedido |
| prioridad | VARCHAR(10) | BAJA, NORMAL, ALTA, URGENTE |
| direccion_origen | VARCHAR(500) | Dirección de recogida |
| latitud_origen | DOUBLE | Coordenada latitud origen |
| longitud_origen | DOUBLE | Coordenada longitud origen |
| direccion_destino | VARCHAR(500) | Dirección de entrega |
| latitud_destino | DOUBLE | Coordenada latitud destino |
| longitud_destino | DOUBLE | Coordenada longitud destino |
| descripcion_paquete | VARCHAR(500) | Descripción del contenido |
| peso_kg | DECIMAL(10,2) | Peso en kilogramos |
| dimensiones | VARCHAR(100) | Dimensiones del paquete |
| tarifa_base | DECIMAL(10,2) | Tarifa base calculada |
| tarifa_total | DECIMAL(10,2) | Tarifa total con recargos |
| fecha_estimada_entrega | TIMESTAMP | Fecha estimada de entrega |
| fecha_entrega_real | TIMESTAMP | Fecha real de entrega |
| observaciones | VARCHAR(1000) | Notas adicionales |
| activo | BOOLEAN | Estado lógico (default: true) |
| fecha_creacion | TIMESTAMP | Fecha de creación |
| fecha_actualizacion | TIMESTAMP | Fecha de actualización |

## Validaciones de Negocio

### Cobertura Geográfica
El servicio valida automáticamente que la distancia entre origen y destino no exceda los límites:
- **URBANA_RAPIDA**: Máximo 20 km
- **INTERMUNICIPAL**: Máximo 150 km
- **NACIONAL**: Sin límite

La distancia se calcula usando la fórmula de Haversine:
```java
d = 2 * R * arcsin(sqrt(sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)))
```
Donde R = 6371 km (radio de la Tierra)

### Transiciones de Estado
- No se puede cancelar un pedido ENTREGADO
- Al marcar como ENTREGADO, se registra automáticamente la fecha real
- La asignación de repartidor cambia el estado a ASIGNADO

### Formato de Número de Pedido
- Formato: `PED-YYYYMMDD-HHMMSS-XXXX`
- Ejemplo: `PED-20240115-143052-7823`
- Garantiza unicidad mediante verificación en BD

## Ejecución

### Prerequisitos
```bash
# Instalar Java 21
# Instalar Maven 3.9+
# Instalar PostgreSQL 16
```

### Crear Base de Datos
```sql
CREATE DATABASE db_logiflow_pedidos;
```

### Compilar y Ejecutar
```bash
# Compilar
./mvnw clean package

# Ejecutar
./mvnw spring-boot:run
```

El servicio estará disponible en: `http://localhost:8083`

## Integración con Otros Servicios

### Dependencias
- **auth-service**: Valida tokens JWT, obtiene información de clientes
- **fleet-service**: Obtiene información de repartidores y vehículos
- **billing-service**: Envía datos de pedidos para facturación

### Referencias Cruzadas
El servicio utiliza UUIDs para referenciar entidades de otros servicios:
- `clienteId` → Usuario en auth-service
- `repartidorId` → Repartidor en fleet-service

## Seguridad
- Validación de entrada con Jakarta Validation
- Transacciones ACID con @Transactional
- Eliminación lógica para auditoría
- Validación de cobertura geográfica

## Logs
El servicio registra logs en nivel DEBUG para:
- Operaciones CRUD
- Validaciones de negocio
- Consultas SQL (Hibernate)

## Próximas Mejoras
- [ ] Integración con Kong API Gateway
- [ ] Autenticación JWT
- [ ] Notificaciones en tiempo real (WebSocket)
- [ ] Cálculo automático de tarifas
- [ ] Optimización de rutas
- [ ] Tests unitarios con JUnit 5
- [ ] Documentación OpenAPI 3.0

## Soporte
Para más información, consulte la documentación del proyecto LogiFlow.

---
**Versión**: 1.0.0  
**Fecha**: Enero 2024  
**Equipo**: LogiFlow Development Team
