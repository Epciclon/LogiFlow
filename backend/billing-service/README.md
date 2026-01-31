# LogiFlow - Billing Service

## Descripción
Microservicio para el cálculo dinámico de tarifas y generación de facturas para servicios de entrega. Implementa un sistema de facturación flexible con múltiples componentes de costo y configuración personalizable.

## Características Principales

### Funcionalidades
- ✅ Cálculo automático de tarifas según tipo de entrega
- ✅ Generación de facturas con número único
- ✅ Componentes de costo: tarifa base + distancia + peso + prioridad
- ✅ Descuentos configurables
- ✅ Cálculo automático de IVA (15%)
- ✅ Gestión de estados de factura (6 estados)
- ✅ Registro de pagos con método
- ✅ Consultas por cliente, pedido y estado
- ✅ Tarifas configurables desde application.yaml
- ✅ Eliminación lógica de registros

### Estados de Factura
- **BORRADOR**: Factura creada, no finalizada
- **PENDIENTE**: Pendiente de pago
- **PAGADA**: Pago completado
- **VENCIDA**: Plazo de pago vencido
- **CANCELADA**: Factura cancelada
- **ANULADA**: Factura anulada

### Estructura de Tarifas

#### Tarifas Base (Configurables)
```yaml
Urbana Rápida:
  Base: $3.00
  Por km: $0.50
  Por kg: $0.20

Intermunicipal:
  Base: $10.00
  Por km: $0.80
  Por kg: $0.30

Nacional:
  Base: $50.00
  Por km: $1.20
  Por kg: $0.50
```

#### Recargos por Prioridad
- **URGENTE**: x2.00
- **ALTA**: x1.50
- **NORMAL**: x1.00
- **BAJA**: x1.00

#### Impuestos
- **IVA**: 15% sobre subtotal

### Fórmula de Cálculo
```
Subtotal = Tarifa Base + (Distancia km × Tarifa/km) + (Peso kg × Tarifa/kg) + Recargo Prioridad - Descuento
IVA = Subtotal × 0.15
Total = Subtotal + IVA
```

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
Puerto PostgreSQL: 5437
Base de datos: db_logiflow_billing
Usuario: postgres
Password: postgres
```

### Puerto del Servicio
```
Puerto: 8085
```

### Configuración de Tarifas (application.yaml)
```yaml
billing:
  tarifas:
    urbana:
      base: 3.00
      por-km: 0.50
      por-kg: 0.20
    intermunicipal:
      base: 10.00
      por-km: 0.80
      por-kg: 0.30
    nacional:
      base: 50.00
      por-km: 1.20
      por-kg: 0.50
  recargos:
    urgente: 2.00
    alta: 1.50
    normal: 1.00
    baja: 1.00
```

## API Endpoints

### Crear Factura
```http
POST /api/facturas
Content-Type: application/json

{
  "pedidoId": "uuid",
  "numeroPedido": "PED-20240115-100000-0001",
  "clienteId": "uuid",
  "clienteNombre": "Juan Pérez",
  "tipoEntrega": "URBANA_RAPIDA",
  "distanciaKm": 8.5,
  "pesoKg": 2.5,
  "prioridad": "NORMAL",
  "descuento": 0.00,
  "fechaVencimiento": "2024-01-30",
  "observaciones": "Factura estándar"
}
```

**Respuesta:**
```json
{
  "id": "uuid",
  "numeroFactura": "FAC-20240115-143052-7823",
  "tarifaBase": 3.00,
  "cargoDistancia": 4.25,
  "cargoPeso": 0.50,
  "recargoPrioridad": 1.00,
  "descuento": 0.00,
  "subtotal": 8.75,
  "impuestoIVA": 1.31,
  "total": 10.06,
  "estado": "BORRADOR"
}
```

### Listar Facturas
```http
GET /api/facturas
```

### Obtener Factura por ID
```http
GET /api/facturas/{id}
```

### Obtener Factura por Número
```http
GET /api/facturas/numero/{numeroFactura}
```

### Obtener Factura por Pedido
```http
GET /api/facturas/pedido/{pedidoId}
```

### Obtener Facturas por Cliente
```http
GET /api/facturas/cliente/{clienteId}
```

### Obtener Facturas por Estado
```http
GET /api/facturas/estado/{estado}
```
Estados: BORRADOR, PENDIENTE, PAGADA, VENCIDA, CANCELADA, ANULADA

### Actualizar Factura
```http
PUT /api/facturas/{id}
Content-Type: application/json

{
  "descuento": 1.50,
  "estado": "PENDIENTE",
  "observaciones": "Descuento aplicado"
}
```

### Cambiar Estado
```http
PATCH /api/facturas/{id}/estado
Content-Type: application/json

{
  "estado": "PENDIENTE"
}
```

### Registrar Pago
```http
PATCH /api/facturas/{id}/pagar
Content-Type: application/json

{
  "metodoPago": "Transferencia Bancaria"
}
```

### Eliminar Factura
```http
DELETE /api/facturas/{id}
```

## Modelo de Datos

### Tabla: factura

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | ID único (PK) |
| numero_factura | VARCHAR(50) | Número único (FAC-YYYYMMDD-HHMMSS-XXXX) |
| pedido_id | UUID | Referencia a pedido-service |
| numero_pedido | VARCHAR(50) | Número del pedido |
| cliente_id | UUID | Referencia a auth-service |
| cliente_nombre | VARCHAR(200) | Nombre del cliente |
| tipo_entrega | VARCHAR(20) | Tipo de entrega del pedido |
| distancia_km | DECIMAL(10,2) | Distancia recorrida |
| peso_kg | DECIMAL(10,2) | Peso del paquete |
| tarifa_base | DECIMAL(10,2) | Tarifa base según tipo |
| cargo_distancia | DECIMAL(10,2) | Cargo calculado por distancia |
| cargo_peso | DECIMAL(10,2) | Cargo calculado por peso |
| recargo_prioridad | DECIMAL(10,2) | Recargo por prioridad |
| descuento | DECIMAL(10,2) | Descuento aplicado |
| subtotal | DECIMAL(10,2) | Suma antes de impuestos |
| impuesto_iva | DECIMAL(10,2) | IVA 15% |
| total | DECIMAL(10,2) | Total a pagar |
| estado | VARCHAR(20) | Estado de la factura |
| fecha_emision | DATE | Fecha de emisión |
| fecha_vencimiento | DATE | Fecha límite de pago |
| fecha_pago | DATE | Fecha de pago efectivo |
| metodo_pago | VARCHAR(50) | Método de pago usado |
| observaciones | VARCHAR(500) | Notas adicionales |
| activo | BOOLEAN | Estado lógico |
| fecha_creacion | TIMESTAMP | Fecha de creación |
| fecha_actualizacion | TIMESTAMP | Fecha de actualización |

## Ejemplos de Cálculo

### Ejemplo 1: Entrega Urbana Normal
```
Tipo: URBANA_RAPIDA
Distancia: 8.5 km
Peso: 2.5 kg
Prioridad: NORMAL

Tarifa Base: $3.00
Cargo Distancia: 8.5 × $0.50 = $4.25
Cargo Peso: 2.5 × $0.20 = $0.50
Recargo Prioridad: $1.00
Descuento: $0.00

Subtotal: $8.75
IVA (15%): $1.31
Total: $10.06
```

### Ejemplo 2: Entrega Nacional Urgente
```
Tipo: NACIONAL
Distancia: 250 km
Peso: 45 kg
Prioridad: URGENTE

Tarifa Base: $50.00
Cargo Distancia: 250 × $1.20 = $300.00
Cargo Peso: 45 × $0.50 = $22.50
Recargo Prioridad: $2.00
Descuento: $10.00

Subtotal: $364.50
IVA (15%): $54.68
Total: $419.18
```

## Validaciones de Negocio

- Un pedido solo puede tener una factura
- El número de factura es único y se genera automáticamente
- Las tarifas se calculan automáticamente según configuración
- El IVA se calcula sobre el subtotal
- Los descuentos se aplican antes del IVA
- Al registrar pago se actualiza automáticamente el estado a PAGADA

## Ejecución

### Prerequisitos
```bash
# Java 21, Maven 3.9+, PostgreSQL 16
```

### Crear Base de Datos
```sql
CREATE DATABASE db_logiflow_billing;
```

### Compilar y Ejecutar
```bash
./mvnw clean package
./mvnw spring-boot:run
```

El servicio estará disponible en: `http://localhost:8085`

## Integración con Otros Servicios

### Referencias Cruzadas
- **pedido-service**: Crea factura basándose en datos del pedido
- **auth-service**: Obtiene información del cliente
- **fleet-service**: Puede calcular costos de repartidores

## Seguridad
- Validación de entrada con Jakarta Validation
- Transacciones ACID con @Transactional
- Eliminación lógica para auditoría
- Cálculos precisos con BigDecimal

## Próximas Mejoras
- [ ] Integración con Kong API Gateway
- [ ] Autenticación JWT
- [ ] Generación de PDF de facturas
- [ ] Integración con pasarelas de pago
- [ ] Reportes de facturación
- [ ] Notificaciones de vencimiento
- [ ] Tests unitarios con JUnit 5
- [ ] Documentación OpenAPI 3.0

---
**Versión**: 1.0.0  
**Fecha**: Diciembre 2024  
**Equipo**: LogiFlow Development Team
