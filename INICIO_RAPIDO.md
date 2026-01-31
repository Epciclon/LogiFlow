# 🚀 Inicio Rápido - LogiFlow con Kong Gateway

## Pasos para Pruebas Finales con API Gateway

### 1️⃣ Levantar la Plataforma Completa

```powershell
# Navegar al directorio del proyecto
cd C:\AppServ\Distribuidas\delivery

# Levantar todos los servicios (4 microservicios + 5 PostgreSQL + Kong Gateway)
docker-compose up -d

# Ver el progreso de los contenedores
docker-compose ps
```

**Esperado**: 10 contenedores corriendo:
- ✅ `auth-service` (8082)
- ✅ `pedido-service` (8083)
- ✅ `fleet-service` (8084)
- ✅ `billing-service` (8085)
- ✅ `logiflow_auth_db` (PostgreSQL 5434)
- ✅ `logiflow_pedidos_db` (PostgreSQL 5435)
- ✅ `logiflow_fleet_db` (PostgreSQL 5436)
- ✅ `logiflow_billing_db` (PostgreSQL 5437)
- ✅ `kong-database` (PostgreSQL 5438)
- ✅ `kong-gateway` (8000, 8001)

### 2️⃣ Verificar que Kong esté Listo

**GET** `http://localhost:8001/status`

Deberías ver: `{"database":{"reachable":true},...}`

### 3️⃣ Configurar Rutas en Kong (AUTOMÁTICO)

```powershell
# Ejecutar script de configuración
./kong-config.ps1
```

Este script configura:
- **Auth Service**: `http://localhost:8000/auth/*`
- **Pedido Service**: `http://localhost:8000/pedidos/*`
- **Fleet Service**: `http://localhost:8000/fleet/*`
- **Billing Service**: `http://localhost:8000/billing/*`

### 4️⃣ Cargar Datos de Prueba

```powershell
# Auth Service - 7 usuarios
docker exec -i logiflow_auth_db psql -U postgres -d db_logiflow_auth < backend/auth-service/test-data.sql

# Pedido Service - 8 pedidos
docker exec -i logiflow_pedidos_db psql -U postgres -d db_logiflow_pedidos < backend/pedido-service/test-data.sql

# Fleet Service - 6 vehículos, 7 repartidores
docker exec -i logiflow_fleet_db psql -U postgres -d db_logiflow_fleet < backend/fleet-service/test-data.sql

# Billing Service - 8 facturas
docker exec -i logiflow_billing_db psql -U postgres -d db_logiflow_billing < backend/billing-service/test-data.sql
```

---

## 🧪 Pruebas con Postman (Kong Gateway - Puerto 8000)

### ✅ Prueba 1: Verificar Servicios

**GET** `http://localhost:8000/auth/`
**GET** `http://localhost:8000/pedidos/`
**GET** `http://localhost:8000/fleet/`
**GET** `http://localhost:8000/billing/`

### ✅ Prueba 2: Login y Obtener Token

**POST** `http://localhost:8000/auth/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Respuesta esperada**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "admin",
  "email": "admin@logiflow.com",
  "roles": ["ADMINISTRADOR"]
}
```

**💡 Guardar el `accessToken` en una variable de Postman:**
1. En la pestaña **Tests** del request, agregar:
```javascript
pm.test("Login exitoso", function () {
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.accessToken);
});
```

### ✅ Prueba 3: Listar Pedidos

**GET** `http://localhost:8000/pedidos/api/pedidos`

**Respuesta esperada**: Lista de 8 pedidos

### ✅ Prueba 4: Listar Repartidores

**GET** `http://localhost:8000/fleet/api/repartidores`

**Respuesta esperada**: Lista de 7 repartidores

### ✅ Prueba 5: Listar Vehículos

**GET** `http://localhost:8000/fleet/api/vehiculos`

**Respuesta esperada**: Lista de 6 vehículos

### ✅ Prueba 6: Listar Facturas

**GET** `http://localhost:8000/billing/api/facturas`

**Respuesta esperada**: Lista de 8 facturas

### ✅ Prueba 7: Crear Pedido (POST)

**POST** `http://localhost:8000/pedidos/api/pedidos`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "clienteId": "c1111111-1111-1111-1111-111111111111",
  "clienteNombre": "Juan Pérez",
  "clienteTelefono": "0991234567",
  "clienteEmail": "cliente1@logiflow.com",
  "direccionOrigen": "Av. Amazonas N24-03",
  "latitudOrigen": -0.1807,
  "longitudOrigen": -78.4678,
  "direccionDestino": "Av. 6 de Diciembre N36-15",
  "latitudDestino": -0.1650,
  "longitudDestino": -78.4822,
  "tipoEntrega": "URBANA_RAPIDA",
  "pesoKg": 2.5,
  "descripcionPaquete": "Documentos urgentes",
  "prioridad": "URGENTE"
}
```

### ✅ Prueba 8: Crear Factura (POST)

**POST** `http://localhost:8000/billing/api/facturas`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "pedidoId": "c1111111-1111-1111-1111-111111111111",
  "numeroPedido": "PED-20240115-100000-0001",
  "clienteId": "a1111111-1111-1111-1111-111111111111",
  "clienteNombre": "Juan Pérez",
  "tipoEntrega": "URBANA_RAPIDA",
  "distanciaKm": 5.0,
  "pesoKg": 2.5,
  "prioridad": "NORMAL",
  "descuento": 0.00,
  "fechaVencimiento": "2024-02-15",
  "observaciones": "Factura de prueba desde Kong"
}
```

---

## 🔍 Verificar Configuración de Kong

### Ver Servicios Registrados
**GET** `http://localhost:8001/services`

### Ver Rutas Configuradas
**GET** `http://localhost:8001/routes`

### Ver Plugins Activos
**GET** `http://localhost:8001/plugins`

---

## 📊 Monitoreo en Tiempo Real

### Ver Logs de Todos los Servicios
```powershell
docker-compose logs -f
```

### Ver Logs de un Servicio Específico
```powershell
docker-compose logs -f auth-service
docker-compose logs -f pedido-service
docker-compose logs -f kong-gateway
```

### Ver Logs de Kong Gateway
```powershell
docker-compose logs -f kong
```

---

## 🛠️ Comandos Útiles

### Reiniciar un Servicio
```powershell
docker-compose restart auth-service
```

### Reconstruir un Servicio
```powershell
docker-compose up -d --build auth-service
```

### Detener Todo
```powershell
docker-compose down
```

### Detener y Eliminar Volúmenes (Limpieza Total)
```powershell
docker-compose down -v
```

### Verificar Estado de Contenedores
```powershell
docker-compose ps
```

---

## 🎯 GUÍA COMPLETA DE PRUEBAS - Todos los Endpoints (60+ Operaciones)
## 📋 PARTE 1: AUTENTICACIÓN Y USUARIOS (Auth Service)

### 1.1️⃣ Información del Servicio

**GET** `http://localhost:8000/auth/`

**Respuesta esperada**: Información del Auth Service con versión 1.0.0

---

### 1.2️⃣ Registro de Nuevo Cliente

**POST** `http://localhost:8000/auth/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "clienteNuevo2025",
  "email": "cliente2025@logiflow.com",
  "password": "SecurePass123!",
  "nombreCompleto": "Cliente Prueba 2025",
  "telefono": "0998765432"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Cliente registrado exitosamente", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.accessToken);
    pm.environment.set("refresh_token", jsonData.refreshToken);
    pm.environment.set("cliente_username", jsonData.username);
});
```

**Guardar**: `access_token`, `refresh_token`

---

### 1.3️⃣ Login como Administrador

**POST** `http://localhost:8000/auth/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Login admin exitoso", function () {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.environment.set("admin_token", jsonData.accessToken);
});
```

**Guardar**: `admin_token`

---

### 1.4️⃣ Renovar Token (Refresh)

**POST** `http://localhost:8000/auth/api/auth/refresh`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "refreshToken": "{{refresh_token}}"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Token renovado", function () {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.accessToken);
});
```

---

## 📦 PARTE 2: GESTIÓN DE PEDIDOS (Pedido Service - 17 Operaciones)

### 2.1️⃣ Información del Servicio

**GET** `http://localhost:8000/pedidos/`

**Respuesta esperada**: Info del Pedido Service en puerto 8083

---

### 2.2️⃣ Crear Pedido Urbano Rápido

**POST** `http://localhost:8000/pedidos/api/pedidos`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "clienteId": "62385819-c2c8-486d-bf9b-04b76108a56d",
  "clienteNombre": "Cliente Prueba 2025",
  "clienteTelefono": "0998765432",
  "clienteEmail": "cliente2025@logiflow.com",
  "direccionOrigen": "Av. Amazonas N24-03, Quito",
  "latitudOrigen": -0.1807,
  "longitudOrigen": -78.4678,
  "direccionDestino": "Av. 6 de Diciembre N36-15, Quito",
  "latitudDestino": -0.1650,
  "longitudDestino": -78.4822,
  "tipoEntrega": "URBANA_RAPIDA",
  "pesoKg": 2.5,
  "descripcionPaquete": "Documentos urgentes",
  "prioridad": "URGENTE",
  "observaciones": "Llamar al llegar"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido urbano creado", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("pedido_urbano_id", jsonData.id);
    pm.environment.set("numero_pedido_urbano", jsonData.numeroPedido);
});
```

**Guardar**: `pedido_urbano_id`, `numero_pedido_urbano`

---

### 2.3️⃣ Crear Pedido Intermunicipal

**POST** `http://localhost:8000/pedidos/api/pedidos`

**Body (raw JSON):**
```json
{
  "clienteId": "615745b2-9a33-46c2-ae3c-86bfa4f7dacb",
  "clienteNombre": "María González",
  "clienteTelefono": "0991234567",
  "clienteEmail": "maria@logiflow.com",
  "direccionOrigen": "Centro Histórico, Quito",
  "latitudOrigen": -0.2201,
  "longitudOrigen": -78.5123,
  "direccionDestino": "Plaza Central, Ambato",
  "latitudDestino": -1.2490,
  "longitudDestino": -78.6167,
  "tipoEntrega": "INTERMUNICIPAL",
  "pesoKg": 5.0,
  "descripcionPaquete": "Paquete mediano",
  "prioridad": "ALTA",
  "observaciones": "Frágil"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido intermunicipal creado", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("pedido_intermunicipal_id", jsonData.id);
});
```

**Guardar**: `pedido_intermunicipal_id`

---

### 2.4️⃣ Crear Pedido Nacional

**POST** `http://localhost:8000/pedidos/api/pedidos`

**Body (raw JSON):**
```json
{
  "clienteId": "a3333333-3333-3333-3333-333333333333",
  "clienteNombre": "Carlos Rodríguez",
  "clienteTelefono": "0987654321",
  "clienteEmail": "carlos@logiflow.com",
  "direccionOrigen": "Parque Industrial, Quito",
  "latitudOrigen": -0.2500,
  "longitudOrigen": -78.5200,
  "direccionDestino": "Zona Industrial, Guayaquil",
  "latitudDestino": -2.1700,
  "longitudDestino": -79.9224,
  "tipoEntrega": "NACIONAL",
  "pesoKg": 50.0,
  "descripcionPaquete": "Equipos industriales",
  "prioridad": "NORMAL",
  "observaciones": "Horario de oficina"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido nacional creado", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("pedido_nacional_id", jsonData.id);
});
```

**Guardar**: `pedido_nacional_id`

---

### 2.5️⃣ Listar Todos los Pedidos

**GET** `http://localhost:8000/pedidos/api/pedidos`

**Respuesta esperada**: Array con 11+ pedidos (8 test-data + 3 nuevos)

---

### 2.6️⃣ Obtener Pedido por ID

**GET** `http://localhost:8000/pedidos/api/pedidos/{{pedido_urbano_id}}`

**Respuesta esperada**: Detalles completos del pedido urbano

---

### 2.7️⃣ Obtener Pedido por Número

**GET** `http://localhost:8000/pedidos/api/pedidos/numero/{{numero_pedido_urbano}}`

**Respuesta esperada**: Mismo pedido pero buscado por número

---

### 2.8️⃣ Listar Pedidos por Cliente

**GET** `http://localhost:8000/pedidos/api/pedidos/cliente/a1111111-1111-1111-1111-111111111111`

**Respuesta esperada**: Array con pedidos del cliente específico

---

### 2.9️⃣ Listar Pedidos por Estado (RECIBIDO)

**GET** `http://localhost:8000/pedidos/api/pedidos/estado/RECIBIDO`

**Respuesta esperada**: Array con pedidos en estado RECIBIDO

---

### 2.🔟 Asignar Repartidor al Pedido

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_urbano_id}}/asignar-repartidor`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "repartidorId": "a1111111-1111-1111-1111-111111111111",
  "repartidorNombre": "Carlos Mendoza"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Repartidor asignado", function () {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.estado).to.eql("ASIGNADO");
});
```

**Respuesta esperada**: Estado cambia a ASIGNADO

---

### 2.1️⃣1️⃣ Cambiar Estado a EN_PREPARACION

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_urbano_id}}/estado`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "estado": "EN_PREPARACION"
}
```

---

### 2.1️⃣2️⃣ Cambiar Estado a EN_RUTA

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_urbano_id}}/estado`

**Body (raw JSON):**
```json
{
  "estado": "EN_RUTA"
}
```

---

### 2.1️⃣3️⃣ Cambiar Estado a ENTREGADO

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_urbano_id}}/estado`

**Body (raw JSON):**
```json
{
  "estado": "ENTREGADO"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido entregado", function () {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.estado).to.eql("ENTREGADO");
    pm.expect(jsonData.fechaEntregaReal).to.not.be.null;
});
```

---

### 2.1️⃣4️⃣ Actualizar Tarifas del Pedido

**PUT** `http://localhost:8000/pedidos/api/pedidos/{{pedido_intermunicipal_id}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "tarifaBase": 10.00,
  "tarifaTotal": 92.46,
  "observaciones": "Tarifas actualizadas por sistema de facturación"
}
```

---

### 2.1️⃣5️⃣ Cancelar Pedido

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_nacional_id}}/cancelar`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "motivo": "Cliente solicitó cancelación"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido cancelado", function () {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.estado).to.eql("CANCELADO");
});
```

---

### 2.1️⃣6️⃣ Crear Pedido para Eliminar

**POST** `http://localhost:8000/pedidos/api/pedidos`

**Body (raw JSON):**
```json
{
  "clienteId": "a1111111-1111-1111-1111-111111111111",
  "clienteNombre": "Test Eliminación",
  "direccionOrigen": "Origen Test",
  "latitudOrigen": -0.1807,
  "longitudOrigen": -78.4678,
  "direccionDestino": "Destino Test",
  "latitudDestino": -0.1650,
  "longitudDestino": -78.4822,
  "tipoEntrega": "URBANA_RAPIDA",
  "pesoKg": 1.0,
  "descripcionPaquete": "Para eliminar",
  "prioridad": "NORMAL"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido test creado", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("pedido_delete_id", jsonData.id);
});
```

---

### 2.1️⃣7️⃣ Eliminar Pedido (Lógico)

**DELETE** `http://localhost:8000/pedidos/api/pedidos/{{pedido_delete_id}}`

**Respuesta esperada**: 204 No Content

---

## 🚛 PARTE 3: GESTIÓN DE FLOTA (Fleet Service - 10+ Operaciones)

### 3.1️⃣ Información del Servicio

**GET** `http://localhost:8000/fleet/`

**Respuesta esperada**: Info del Fleet Service en puerto 8084

---

### 3.2️⃣ Listar Todos los Repartidores

**GET** `http://localhost:8000/fleet/api/repartidores`

**Respuesta esperada**: Array con 7 repartidores de test-data

---

### 3.3️⃣ Obtener Repartidor por ID

**GET** `http://localhost:8000/fleet/api/repartidores/a1111111-1111-1111-1111-111111111111`

**Respuesta esperada**: Detalles de Carlos Mendoza

---

### 3.4️⃣ Listar Repartidores Disponibles

**GET** `http://localhost:8000/fleet/api/repartidores/disponibles`

**Respuesta esperada**: Array con repartidores activos y disponibles

---

### 3.5️⃣ Actualizar Ubicación del Repartidor

**PATCH** `http://localhost:8000/fleet/api/repartidores/a1111111-1111-1111-1111-111111111111/ubicacion`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "coordenadas": "-0.1750,-78.4700"
}
```

**Respuesta esperada**: Ubicación actualizada

---

### 3.6️⃣ Listar Todos los Vehículos

**GET** `http://localhost:8000/fleet/api/vehiculos`

**Respuesta esperada**: Array con 6 vehículos de test-data

---

### 3.7️⃣ Obtener Vehículo por ID

**GET** `http://localhost:8000/fleet/api/vehiculos/v1111111-1111-1111-1111-111111111111`

**Respuesta esperada**: Detalles de Moto Honda CRF 250

---

### 3.8️⃣ Listar Vehículos Disponibles

**GET** `http://localhost:8000/fleet/api/vehiculos/disponibles`

**Respuesta esperada**: Array con vehículos activos y disponibles

---

### 3.9️⃣ Listar Vehículos por Tipo

**GET** `http://localhost:8000/fleet/api/vehiculos/tipo/CAMIONETA`

**Respuesta esperada**: Array con camionetas

**Tipos válidos**: MOTO, AUTO, CAMIONETA, CAMION

---

### 3.🔟 Obtener Pedidos del Repartidor

**GET** `http://localhost:8000/pedidos/api/pedidos/repartidor/a1111111-1111-1111-1111-111111111111`

**Respuesta esperada**: Array con pedidos asignados a Carlos Mendoza

---

## 💰 PARTE 4: FACTURACIÓN COMPLETA (Billing Service - 12 Operaciones)

### 4.1️⃣ Información del Servicio

**GET** `http://localhost:8000/billing/`

**Respuesta esperada**: Info del Billing Service con tarifas

---

### 4.2️⃣ Crear Factura para Pedido Urbano

**POST** `http://localhost:8000/billing/api/facturas`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "pedidoId": "{{pedido_urbano_id}}",
  "numeroPedido": "{{numero_pedido_urbano}}",
  "clienteId": "a1111111-1111-1111-1111-111111111111",
  "clienteNombre": "Cliente Prueba 2025",
  "tipoEntrega": "URBANA_RAPIDA",
  "distanciaKm": 5.0,
  "pesoKg": 2.5,
  "prioridad": "URGENTE",
  "descuento": 0.00,
  "fechaVencimiento": "2025-01-30",
  "observaciones": "Factura de prueba completa"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Factura creada", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("factura_id", jsonData.id);
    pm.environment.set("numero_factura", jsonData.numeroFactura);
    pm.expect(jsonData.numeroFactura).to.match(/^FAC-\d{8}-\d{6}-\d{4}$/);
});
```

**Guardar**: `factura_id`, `numero_factura`

---

### 4.3️⃣ Crear Factura Intermunicipal con Descuento

**POST** `http://localhost:8000/billing/api/facturas`

**Body (raw JSON):**
```json
{
  "pedidoId": "{{pedido_intermunicipal_id}}",
  "numeroPedido": "PED-TEST-002",
  "clienteId": "a2222222-2222-2222-2222-222222222222",
  "clienteNombre": "María González",
  "tipoEntrega": "INTERMUNICIPAL",
  "distanciaKm": 95.0,
  "pesoKg": 5.0,
  "prioridad": "ALTA",
  "descuento": 10.00,
  "fechaVencimiento": "2025-02-15",
  "observaciones": "Cliente frecuente - Descuento aplicado"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Factura intermunicipal creada", function () {
    pm.response.to.have.status(201);
    var jsonData = pm.response.json();
    pm.environment.set("factura_intermunicipal_id", jsonData.id);
});
```

---

### 4.4️⃣ Listar Todas las Facturas

**GET** `http://localhost:8000/billing/api/facturas`

**Respuesta esperada**: Array con 10+ facturas (8 test-data + 2 nuevas)

---

### 4.5️⃣ Obtener Factura por ID

**GET** `http://localhost:8000/billing/api/facturas/{{factura_id}}`

**Respuesta esperada**: Detalles completos de la factura

---

### 4.6️⃣ Obtener Factura por Número

**GET** `http://localhost:8000/billing/api/facturas/numero/{{numero_factura}}`

**Respuesta esperada**: Misma factura buscada por número

---

### 4.7️⃣ Obtener Factura por Pedido

**GET** `http://localhost:8000/billing/api/facturas/pedido/{{pedido_urbano_id}}`

**Respuesta esperada**: Factura asociada al pedido

---

### 4.8️⃣ Listar Facturas por Cliente

**GET** `http://localhost:8000/billing/api/facturas/cliente/a1111111-1111-1111-1111-111111111111`

**Respuesta esperada**: Array con facturas del cliente

---

### 4.9️⃣ Listar Facturas por Estado (PENDIENTE)

**GET** `http://localhost:8000/billing/api/facturas/estado/PENDIENTE`

**Respuesta esperada**: Array con facturas pendientes

**Estados válidos**: BORRADOR, PENDIENTE, PAGADA, VENCIDA, CANCELADA, ANULADA

---

### 4.🔟 Actualizar Factura (Cambiar Descuento)

**PUT** `http://localhost:8000/billing/api/facturas/{{factura_id}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "descuento": 2.00,
  "observaciones": "Descuento adicional aplicado - Promoción"
}
```

**Respuesta esperada**: Factura con subtotal e IVA recalculados

---

### 4.1️⃣1️⃣ Cambiar Estado a PENDIENTE

**PATCH** `http://localhost:8000/billing/api/facturas/{{factura_id}}/estado`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "estado": "PENDIENTE"
}
```

**Respuesta esperada**: Estado actualizado a PENDIENTE

---

### 4.1️⃣2️⃣ Registrar Pago

**PATCH** `http://localhost:8000/billing/api/facturas/{{factura_id}}/pagar`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "metodoPago": "Tarjeta de Crédito"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pago registrado", function () {
    pm.response.to.have.status(200);
    var jsonData = pm.response.json();
    pm.expect(jsonData.estado).to.eql("PAGADA");
    pm.expect(jsonData.fechaPago).to.not.be.null;
    pm.expect(jsonData.metodoPago).to.eql("Tarjeta de Crédito");
});
```

**Respuesta esperada**: Estado cambia a PAGADA, fechaPago registrada

---

## ✅ VERIFICACIÓN FINAL

### ✔️ Resumen de Operaciones Completadas

**Auth Service (4)**:
- ✅ Info del servicio
- ✅ Registro de cliente
- ✅ Login admin
- ✅ Refresh token

**Pedido Service (17)**:
- ✅ Info del servicio
- ✅ 3 pedidos creados (urbano, intermunicipal, nacional)
- ✅ Listados (todos, por ID, por número, por cliente, por estado)
- ✅ Asignar repartidor
- ✅ Cambios de estado (4 transiciones)
- ✅ Actualización de tarifas
- ✅ Cancelación
- ✅ Eliminación

**Fleet Service (10)**:
- ✅ Info del servicio
- ✅ Listados de repartidores (todos, por ID, disponibles)
- ✅ Actualización de ubicación
- ✅ Listados de vehículos (todos, por ID, disponibles, por tipo)
- ✅ Pedidos por repartidor

**Billing Service (12)**:
- ✅ Info del servicio
- ✅ 2 facturas creadas
- ✅ Listados (todas, por ID, por número, por pedido, por cliente, por estado)
- ✅ Actualización de factura
- ✅ Cambio de estado
- ✅ Registro de pago

**TOTAL**: ~43 endpoints probados ✅

---

## 🎯 Flujo de Trabajo Completo (Prueba End-to-End con Postman)

### 1️⃣ Login (Administrador)

**POST** `http://localhost:8000/auth/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Login exitoso", function () {
    var jsonData = pm.response.json();
    pm.environment.set("access_token", jsonData.accessToken);
});
```

---

### 2️⃣ Crear Cliente (Registro)

**POST** `http://localhost:8000/auth/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "clientePrueba",
  "email": "prueba@logiflow.com",
  "password": "password123",
  "nombreCompleto": "Cliente de Prueba"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Cliente registrado", function () {
    var jsonData = pm.response.json();
    pm.environment.set("cliente_id", jsonData.id);
});
```

---

### 3️⃣ Crear Pedido

**POST** `http://localhost:8000/pedidos/api/pedidos`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "clienteId": "{{cliente_id}}",
  "clienteNombre": "Cliente de Prueba",
  "clienteTelefono": "0991234567",
  "clienteEmail": "prueba@logiflow.com",
  "direccionOrigen": "Av. Amazonas N24-03",
  "latitudOrigen": -0.1807,
  "longitudOrigen": -78.4678,
  "direccionDestino": "Av. 6 de Diciembre N36-15",
  "latitudDestino": -0.1650,
  "longitudDestino": -78.4822,
  "tipoEntrega": "URBANA_RAPIDA",
  "pesoKg": 3.0,
  "descripcionPaquete": "Paquete de prueba",
  "prioridad": "NORMAL"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Pedido creado", function () {
    var jsonData = pm.response.json();
    pm.environment.set("pedido_id", jsonData.id);
});
```

---

### 4️⃣ Asignar Repartidor al Pedido

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_id}}/asignar-repartidor`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "repartidorId": "a1111111-1111-1111-1111-111111111111",
  "repartidorNombre": "Carlos Mendoza"
}
```

---

### 5️⃣ Generar Factura del Pedido

**POST** `http://localhost:8000/billing/api/facturas`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "pedidoId": "{{pedido_id}}",
  "numeroPedido": "PED-TEST-001",
  "clienteId": "{{cliente_id}}",
  "clienteNombre": "Cliente de Prueba",
  "tipoEntrega": "URBANA_RAPIDA",
  "distanciaKm": 5.0,
  "pesoKg": 3.0,
  "prioridad": "NORMAL",
  "descuento": 0.00,
  "fechaVencimiento": "2024-02-15",
  "observaciones": "Factura de prueba end-to-end"
}
```

**Tests (Postman Script):**
```javascript
pm.test("Factura generada", function () {
    var jsonData = pm.response.json();
    pm.environment.set("factura_id", jsonData.id);
});
```

---

### 6️⃣ Registrar Pago de Factura

**PATCH** `http://localhost:8000/billing/api/facturas/{{factura_id}}/pagar`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "metodoPago": "Tarjeta de Crédito"
}
```

---

### 7️⃣ Cambiar Estado del Pedido a "En Ruta"

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_id}}/estado`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nuevoEstado": "EN_RUTA"
}
```

---

### 8️⃣ Actualizar Ubicación del Repartidor

**PATCH** `http://localhost:8000/fleet/api/repartidores/a1111111-1111-1111-1111-111111111111/ubicacion`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "coordenadas": "-0.1700,-78.4750"
}
```

---

### 9️⃣ Marcar Pedido como Entregado

**PATCH** `http://localhost:8000/pedidos/api/pedidos/{{pedido_id}}/estado`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nuevoEstado": "ENTREGADO"
}
```

---

### 🔟 Consultar Factura Pagada

**GET** `http://localhost:8000/billing/api/facturas/{{factura_id}}`

**Respuesta esperada:**
```json
{
  "id": "...",
  "numeroFactura": "FAC-...",
  "estado": "PAGADA",
  "metodoPago": "Tarjeta de Crédito",
  "totalAPagar": 6.50,
  ...
}
```

---

## 🌐 Acceso Directo vs Kong Gateway

### Acceso Directo (Sin Gateway)
- Auth: `http://localhost:8082/api/auth/login`
- Pedidos: `http://localhost:8083/api/pedidos`
- Fleet: `http://localhost:8084/api/repartidores`
- Billing: `http://localhost:8085/api/facturas`

### A través de Kong Gateway (Recomendado)
- Auth: `http://localhost:8000/auth/api/auth/login`
- Pedidos: `http://localhost:8000/pedidos/api/pedidos`
- Fleet: `http://localhost:8000/fleet/api/repartidores`
- Billing: `http://localhost:8000/billing/api/facturas`

**Ventajas de usar Kong**:
- ✅ Punto de entrada único (puerto 8000)
- ✅ Rate limiting
- ✅ CORS configurado
- ✅ Logging centralizado
- ✅ Load balancing
- ✅ Autenticación centralizada (futuro)

---

## 📝 Notas Importantes

1. **Primera vez**: Los contenedores tardan ~2-3 minutos en estar completamente operativos
2. **Kong Gateway**: Debe estar en estado "healthy" antes de configurar rutas
3. **Bases de datos**: Se crean automáticamente con `ddl-auto: update`
4. **Datos de prueba**: Cárgalos después de que los servicios estén corriendo
5. **Puertos**: Asegúrate de que los puertos 8000, 8001, 8082-8085, 5434-5438 estén libres
6. **Variables Postman**: Usa variables de entorno para `{{access_token}}`, `{{cliente_id}}`, `{{pedido_id}}`, `{{factura_id}}`

---

## 🆘 Solución de Problemas

### Kong no inicia
```powershell
# Ver logs de Kong
docker-compose logs kong

# Verificar que kong-database esté healthy
docker-compose ps kong-database

# Reiniciar Kong
docker-compose restart kong
```

### Servicio no responde
```powershell
# Ver logs del servicio
docker-compose logs auth-service

# Verificar que la BD esté healthy
docker-compose ps logiflow_auth_db

# Reiniciar el servicio
docker-compose restart auth-service
```

### Error de conexión a BD
```powershell
# Verificar que las BDs estén corriendo
docker-compose ps | findstr postgres

# Verificar logs de la BD
docker-compose logs logiflow_auth_db
```

---

## 📖 Documentación OpenAPI (Swagger UI)

Cada microservicio expone su contrato OpenAPI 3.0 en Swagger UI.

**⚠️ Importante**: Swagger UI solo está disponible mediante **acceso directo** a cada servicio (no a través de Kong Gateway).

### Acceso Directo (Recomendado para Documentación)
- **Auth Service**: http://localhost:8082/swagger-ui.html
- **Pedido Service**: http://localhost:8083/swagger-ui.html
- **Fleet Service**: http://localhost:8084/swagger-ui.html
- **Billing Service**: http://localhost:8085/swagger-ui.html

### Descargar OpenAPI JSON
- **Auth**: http://localhost:8082/v3/api-docs
- **Pedido**: http://localhost:8083/v3/api-docs
- **Fleet**: http://localhost:8084/v3/api-docs
- **Billing**: http://localhost:8085/v3/api-docs

**💡 Tip**: Usa Swagger UI para explorar los endpoints disponibles, ver ejemplos de request/response y probar llamadas directas. Para pruebas a través del Gateway, usa Postman con los ejemplos de este documento.

---

## ✅ Checklist de Verificación Final

- [ ] 10 contenedores corriendo (`docker-compose ps`)
- [ ] Kong Gateway en estado "healthy"
- [ ] Datos de prueba cargados (28 registros totales)
- [ ] Login exitoso con usuario `admin`
- [ ] Listar pedidos devuelve 8 registros
- [ ] Crear pedido funciona correctamente
- [ ] Asignar repartidor actualiza el estado
- [ ] Generar factura calcula correctamente
- [ ] Registrar pago marca factura como "PAGADA"
- [ ] Swagger UI accesible en los 4 servicios

---

## 🎓 Próximos Pasos

1. **Monitoreo**: Agregar Prometheus + Grafana
2. **Trazabilidad**: Implementar Zipkin/Jaeger
3. **Seguridad**: JWT con Kong JWT Plugin
4. **CI/CD**: GitHub Actions para tests automáticos
5. **Kubernetes**: Migrar a AKS/EKS
6. **Caché**: Redis para sesiones
7. **Mensajería**: RabbitMQ para eventos asincrónicos

---

## 📚 Referencias

- **Kong Gateway**: https://docs.konghq.com/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **OpenAPI 3.0**: https://swagger.io/specification/
- **Docker Compose**: https://docs.docker.com/compose/
- **PostgreSQL**: https://www.postgresql.org/docs/
    "username": "maria.lopez",
    "password": "SecurePass123!"
  }'
```

**Guardar el `accessToken` de la respuesta** (lo usaremos como `$TOKEN`)

### Paso 3: Crear Pedido

```powershell
# Reemplaza $TOKEN con el token obtenido en el paso anterior
curl -X POST http://localhost:8000/pedidos/api/pedidos `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "clienteNombre": "María López García",
    "tipoEntrega": "URBANA_RAPIDA",
    "prioridad": "ALTA",
    "direccionOrigen": "Av. Amazonas N24-155, Quito",
    "latitudOrigen": -0.1807,
    "longitudOrigen": -78.4678,
    "direccionDestino": "Av. 6 de Diciembre N34-234, Quito",
    "latitudDestino": -0.1650,
    "longitudDestino": -78.4822,
    "pesoKg": 3.5,
    "descripcionPaquete": "Laptop Dell XPS 15",
    "valorDeclarado": 1200.00
  }'
```

**Guardar el `id` del pedido de la respuesta** (lo usaremos como `$PEDIDO_ID`)

### Paso 4: Listar Repartidores Disponibles

```powershell
curl http://localhost:8000/fleet/api/repartidores/disponibles `
  -H "Authorization: Bearer $TOKEN"
```

**Seleccionar un repartidor disponible** y guardar su `id` como `$REPARTIDOR_ID`

### Paso 5: Asignar Repartidor al Pedido

```powershell
# Reemplaza $PEDIDO_ID y $REPARTIDOR_ID con los valores reales
curl -X PATCH "http://localhost:8000/pedidos/api/pedidos/$PEDIDO_ID/asignar-repartidor" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "repartidorId": "$REPARTIDOR_ID",
    "repartidorNombre": "Carlos Méndez"
  }'
```

**Estado del pedido cambia a**: `ASIGNADO`

### Paso 6: Generar Factura

```powershell
curl -X POST http://localhost:8000/billing/api/facturas `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "pedidoId": "$PEDIDO_ID",
    "numeroPedido": "PED-20241216-000009",
    "clienteNombre": "María López García",
    "tipoEntrega": "URBANA_RAPIDA",
    "distanciaKm": 5.2,
    "pesoKg": 3.5,
    "prioridad": "ALTA",
    "descuento": 0
  }'
```

**Guardar el `id` de la factura** como `$FACTURA_ID`

### Paso 7: Consultar Factura Generada

```powershell
curl http://localhost:8000/billing/api/facturas/$FACTURA_ID `
  -H "Authorization: Bearer $TOKEN"
```

**Revisar**: `subtotal`, `impuestoIVA`, `total`, `estado` (debe ser `PENDIENTE`)

### Paso 8: Registrar Pago

```powershell
curl -X PATCH "http://localhost:8000/billing/api/facturas/$FACTURA_ID/pagar" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "metodoPago": "TARJETA_CREDITO"
  }'
```

**Estado de la factura cambia a**: `PAGADA`, se registra `fechaPago`

### Paso 9: Actualizar Estado del Pedido (En Ruta)

```powershell
curl -X PATCH "http://localhost:8000/pedidos/api/pedidos/$PEDIDO_ID/cambiar-estado" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "nuevoEstado": "EN_RUTA"
  }'
```

### Paso 10: Marcar Pedido como Entregado

```powershell
curl -X PATCH "http://localhost:8000/pedidos/api/pedidos/$PEDIDO_ID/cambiar-estado" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "nuevoEstado": "ENTREGADO"
  }'
```

**¡Ciclo completo finalizado! ✅**

---

## 📊 Verificar OpenAPI / Swagger UI

Puedes explorar las APIs interactivamente en:

- **Auth Service**: http://localhost:8082/swagger-ui.html
- **Pedido Service**: http://localhost:8083/swagger-ui.html
- **Fleet Service**: http://localhost:8084/swagger-ui.html
- **Billing Service**: http://localhost:8085/swagger-ui.html

O a través de Kong Gateway:

- http://localhost:8000/auth/swagger-ui.html
- http://localhost:8000/pedidos/swagger-ui.html
- http://localhost:8000/fleet/swagger-ui.html
- http://localhost:8000/billing/swagger-ui.html

**Contrato OpenAPI JSON**: Reemplaza `/swagger-ui.html` con `/v3/api-docs`

---

## ✅ Checklist de Verificación

- [ ] Docker Desktop está corriendo
- [ ] Puertos 8000, 8001, 8082-8085, 5434-5438 están libres
- [ ] `docker-compose up -d` ejecutado exitosamente
- [ ] 10 contenedores en estado "running"
- [ ] Kong está "healthy": `curl http://localhost:8001/status`
- [ ] Script `kong-config.ps1` ejecutado exitosamente
- [ ] Datos de prueba cargados en las 4 BDs
- [ ] Endpoints responden a través de Kong (puerto 8000)

---

**¡Listo para las pruebas finales! 🎉**

Para cualquier consulta, revisa:
- [README.md](README.md) - Documentación general
- [KONG_SETUP.md](KONG_SETUP.md) - Guía detallada de Kong
- [backend/*/POSTMAN_EXAMPLES.txt](backend/) - Ejemplos de API
