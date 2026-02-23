# 🔐 Credenciales y Configuración Completa - LogiFlow Kubernetes

## 📊 Resumen del Sistema

- **Entorno**: Minikube v1.37.0
- **Namespace**: `logiflow`
- **Total de Pods**: 13 (12 servicios + Kong Gateway)
- **Kong Gateway**: Puerto 8000 (API Gateway principal)
- **Bases de Datos PostgreSQL**: 5 instancias
- **RabbitMQ**: 1 instancia
- **Fecha de última actualización**: 2026-02-05

---

## 🗄️ Bases de Datos PostgreSQL

### 1. Auth Service Database
```yaml
Host (interno): postgres-auth.logiflow.svc.cluster.local
Puerto interno: 5432
Database: auth_db
Usuario: logiflow
Contraseña: logiflow
Tablas: usuarios, usuario_roles
```

**Conectar con pgAdmin (Port-Forward)**:
```powershell
kubectl port-forward -n logiflow svc/postgres-auth 5433:5432
```
- Host: localhost
- Puerto: 5433
- Database: auth_db
- Usuario: logiflow
- Contraseña: logiflow

**Conectar directamente con kubectl**:
```powershell
kubectl exec -it -n logiflow deployment/postgres-auth -- psql -U logiflow -d auth_db
```

**Usuarios de prueba registrados**:
- `usuario2` / `usuario2@example.com` - Rol: CLIENTE
- `admin` / `admin@example.com` - Rol: CLIENTE

---

### 2. Pedido Service Database
```yaml
Host (interno): postgres-pedido.logiflow.svc.cluster.local
Puerto interno: 5432
Database: db_pedidos
Usuario: logiflow
Contraseña: logiflow
Tablas: pedidos, pedido_eventos
```

**Conectar con pgAdmin (Port-Forward)**:
```powershell
kubectl port-forward -n logiflow svc/postgres-pedido 5434:5432
```
- Host: localhost
- Puerto: 5434
- Database: db_pedidos
- Usuario: logiflow
- Contraseña: logiflow

**Conectar directamente con kubectl**:
```powershell
kubectl exec -it -n logiflow deployment/postgres-pedido -- psql -U logiflow -d db_pedidos
```

---

### 3. Fleet Service Database
```yaml
Host (interno): postgres-fleet.logiflow.svc.cluster.local
Puerto interno: 5432
Database: db_fleet
Usuario: logiflow
Contraseña: logiflow
Tablas: drivers, vehicles, zonas_cobertura
```

**Conectar con pgAdmin (Port-Forward)**:
```powershell
kubectl port-forward -n logiflow svc/postgres-fleet 5435:5432
```
- Host: localhost
- Puerto: 5435
- Database: db_fleet
- Usuario: logiflow
- Contraseña: logiflow

**Conectar directamente con kubectl**:
```powershell
kubectl exec -it -n logiflow deployment/postgres-fleet -- psql -U logiflow -d db_fleet
```

---

### 4. Billing Service Database
```yaml
Host (interno): postgres-billing.logiflow.svc.cluster.local
Puerto interno: 5432
Database: db_billing
Usuario: logiflow
Contraseña: logiflow
Tablas: invoices, payment_transactions
```

**Conectar con pgAdmin (Port-Forward)**:
```powershell
kubectl port-forward -n logiflow svc/postgres-billing 5436:5432
```
- Host: localhost
- Puerto: 5436
- Database: db_billing
- Usuario: logiflow
- Contraseña: logiflow

**Conectar directamente con kubectl**:
```powershell
kubectl exec -it -n logiflow deployment/postgres-billing -- psql -U logiflow -d db_billing
```

---

### 5. Notification Service Database
```yaml
Host (interno): postgres-notification.logiflow.svc.cluster.local
Puerto interno: 5432
Database: db_notifications
Usuario: logiflow
Contraseña: logiflow
Tablas: notifications, notification_templates
```

**Conectar con pgAdmin (Port-Forward)**:
```powershell
kubectl port-forward -n logiflow svc/postgres-notification 5437:5432
```
- Host: localhost
- Puerto: 5437
- Database: db_notifications
- Usuario: logiflow
- Contraseña: logiflow

**Conectar directamente con kubectl**:
```powershell
kubectl exec -it -n logiflow deployment/postgres-notification -- psql -U logiflow -d db_notifications
```

---

## 🐰 RabbitMQ

```yaml
Host (interno): rabbitmq.logiflow.svc.cluster.local
Puerto AMQP: 5672
Puerto Management UI: 15672
Usuario: guest
Contraseña: guest
```

### Acceso a Management UI (Port-Forward)
```powershell
kubectl port-forward -n logiflow svc/rabbitmq 15672:15672
```
Abrir en navegador: http://localhost:15672
- Usuario: `guest`
- Contraseña: `guest`

### Acceso a Management UI (NodePort)
```bash
# El servicio RabbitMQ está expuesto en NodePort 30672
minikube service rabbitmq -n logiflow --url
```

### Intercambios (Exchanges) configurados
- **pedidos.eventos**: Eventos de pedidos (pedido.creado, pedido.asignado, etc.)
- **fleet.eventos**: Eventos de flota (driver.asignado, vehiculo.actualizado, etc.)
- **billing.eventos**: Eventos de facturación (invoice.generada, pago.procesado, etc.)

### Colas (Queues) principales
- `pedido.creado.queue`: Notificaciones de nuevos pedidos
- `driver.asignado.queue`: Asignación de repartidores
- `invoice.generada.queue`: Facturas generadas

---

## 🔑 JWT Configuration

```yaml
Secret (hex): 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
Secret (base64): NDA0RTYzNTI2NjU1NkE1ODZFMzI3MjM1NzUzODc4MkY0MTNGNDQyODQ3MkI0QjYyNTA2NDUzNjc1NjZCNTk3MA==
Algoritmo: HMAC-SHA512 (HS512)
Access Token Validity: 3600000 ms (1 hora)
Refresh Token Validity: 604800000 ms (7 días)
Issuer: LogiFlow
```

**Ubicación en Kubernetes**: Secret `app-secrets` en namespace `logiflow`

```powershell
# Ver el JWT_SECRET actual
kubectl get secret app-secrets -n logiflow -o jsonpath='{.data.JWT_SECRET}' | base64 -d
```

---

## 🌐 Kong Gateway

### Información del Servicio
```yaml
Namespace: logiflow
Service: kong-gateway
Proxy Port (interno): 8000
Admin API Port (interno): 8001
NodePorts:
  - Proxy: 30800
  - Admin API: 30801
  - SSL: 30843
```

### Acceso desde localhost
```bash
# Proxy (API Gateway)
http://localhost:8000

# Admin API
http://localhost:8001
```

### Rutas Configuradas

#### 1. Auth Service
```
Endpoint: POST http://localhost:8000/auth/api/auth/register
Endpoint: POST http://localhost:8000/auth/api/auth/login
Backend: http://auth-service:8082
Strip Path: true
```

#### 2. Pedido Service
```
Endpoint: POST http://localhost:8000/pedidos/api/pedidos
Endpoint: GET http://localhost:8000/pedidos/api/pedidos
Backend: http://pedido-service:8083
Strip Path: true
Autenticación: JWT requerido
```

#### 3. Fleet Service
```
Endpoint: POST http://localhost:8000/fleet/api/drivers
Endpoint: GET http://localhost:8000/fleet/api/drivers
Backend: http://fleet-service:8084
Strip Path: true
Autenticación: JWT requerido
```

#### 4. Billing Service
```
Endpoint: GET http://localhost:8000/billing/api/invoices
Backend: http://billing-service:8085
Strip Path: true
Autenticación: JWT requerido
```

#### 5. GraphQL Gateway
```
Endpoint: POST http://localhost:8000/graphql
Backend: http://graphql-gateway:4000
Strip Path: false
```

### Plugins Globales
- **Rate Limiting**: 100 requests por minuto
- **CORS**: Habilitado para todos los orígenes (*)

### Verificar configuración de Kong
```powershell
# Listar servicios
curl http://localhost:8001/services

# Listar rutas
curl http://localhost:8001/routes

# Listar plugins
curl http://localhost:8001/plugins
```

---

## 🔗 Ingress NGINX

```yaml
Namespace: logiflow
Controller: ingress-nginx-controller
Address: 192.168.49.2 (IP de Minikube)
```

### Hosts Configurados

#### 1. api.logiflow.local → Kong Gateway
```
Host: api.logiflow.local
Backend: kong-gateway:8000
Path: /
```

#### 2. graphql.logiflow.local → GraphQL Gateway
```
Host: graphql.logiflow.local
Backend: graphql-gateway:4000
Path: /
```

#### 3. rabbitmq.logiflow.local → RabbitMQ Management
```
Host: rabbitmq.logiflow.local
Backend: rabbitmq:15672
Path: /
```

### Configurar /etc/hosts (Windows)

**Archivo**: `C:\Windows\System32\drivers\etc\hosts`

Agregar las siguientes líneas:
```
192.168.49.2 api.logiflow.local
192.168.49.2 graphql.logiflow.local
192.168.49.2 rabbitmq.logiflow.local
```

### Iniciar Minikube Tunnel
```powershell
# En una terminal separada (mantener abierta)
minikube tunnel
```

### Probar Ingress
```powershell
# Auth service a través de Ingress
curl http://api.logiflow.local/auth/api/auth/login

# GraphQL Gateway
curl http://graphql.logiflow.local/

# RabbitMQ Management UI
# Abrir en navegador: http://rabbitmq.logiflow.local
```

---

## 📝 Scripts de Port-Forward para Todas las Bases de Datos

### Script PowerShell: `port-forward-dbs.ps1`

```powershell
# Port-Forward para todas las bases de datos
# Guardar como: port-forward-dbs.ps1

Write-Host "Iniciando port-forwards para todas las bases de datos..." -ForegroundColor Green

$jobs = @(
    @{Name="Auth DB"; Port=5433; Service="postgres-auth"}
    @{Name="Pedido DB"; Port=5434; Service="postgres-pedido"}
    @{Name="Fleet DB"; Port=5435; Service="postgres-fleet"}
    @{Name="Billing DB"; Port=5436; Service="postgres-billing"}
    @{Name="Notification DB"; Port=5437; Service="postgres-notification"}
    @{Name="RabbitMQ"; Port=15672; Service="rabbitmq"}
)

foreach ($job in $jobs) {
    $scriptBlock = {
        param($service, $port)
        kubectl port-forward -n logiflow svc/$service $port`:5432
    }
    
    if ($job.Service -eq "rabbitmq") {
        $scriptBlock = {
            param($service, $port)
            kubectl port-forward -n logiflow svc/$service $port`:15672
        }
    }
    
    Start-Job -Name $job.Name -ScriptBlock $scriptBlock -ArgumentList $job.Service, $job.Port | Out-Null
    Write-Host "✓ $($job.Name) iniciado en puerto $($job.Port)" -ForegroundColor Cyan
}

Write-Host "`nPort-forwards activos:" -ForegroundColor Yellow
Get-Job | Format-Table -AutoSize

Write-Host "`nPara detener todos los port-forwards:" -ForegroundColor Yellow
Write-Host "Get-Job | Stop-Job; Get-Job | Remove-Job" -ForegroundColor White

Write-Host "`nConexiones de pgAdmin:" -ForegroundColor Green
Write-Host "Auth DB       -> localhost:5433" -ForegroundColor White
Write-Host "Pedido DB     -> localhost:5434" -ForegroundColor White
Write-Host "Fleet DB      -> localhost:5435" -ForegroundColor White
Write-Host "Billing DB    -> localhost:5436" -ForegroundColor White
Write-Host "Notification  -> localhost:5437" -ForegroundColor White
Write-Host "RabbitMQ UI   -> http://localhost:15672" -ForegroundColor White
```

### Ejecutar el script
```powershell
.\port-forward-dbs.ps1
```

### Detener todos los port-forwards
```powershell
Get-Job | Stop-Job
Get-Job | Remove-Job
```

---

## 🧪 Ejemplos de Uso

### 1. Registro de Usuario

```powershell
$registerBody = @{
    username = "testuser"
    email = "testuser@example.com"
    password = "Test1234"
    nombreCompleto = "Usuario de Prueba"
    telefono = "+593987654321"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8000/auth/api/auth/register" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"} `
    -Body $registerBody `
    -UseBasicParsing | Select-Object -ExpandProperty Content
```

### 2. Login y Obtener Token

```powershell
$loginBody = @{
    username = "usuario2"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8000/auth/api/auth/login" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"} `
    -Body $loginBody `
    -UseBasicParsing | Select-Object -ExpandProperty Content | ConvertFrom-Json

$token = $response.accessToken
Write-Host "Token obtenido: $token"
```

### 3. Crear Pedido (con Autenticación)

```powershell
$pedidoBody = @{
    clienteId = "b860d4f4-1eb3-42b1-8298-c11da479dd46"
    clienteNombre = "Juan Pérez"
    tipoEntrega = "URBANA_RAPIDA"
    prioridad = "NORMAL"
    direccionOrigen = "Av. 6 de Diciembre N36-109, Quito"
    latitudOrigen = -0.1807
    longitudOrigen = -78.4678
    direccionDestino = "Av. Naciones Unidas E10-13, Quito"
    latitudDestino = -0.1900
    longitudDestino = -78.4800
    zonaId = "170150"
    descripcionPaquete = "Documentos urgentes"
    pesoKg = 0.5
    dimensiones = "30x20x5 cm"
    observaciones = "Llamar al llegar"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8000/pedidos/api/pedidos" `
    -Method POST `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    } `
    -Body $pedidoBody `
    -UseBasicParsing | Select-Object -ExpandProperty Content
```

### 4. Listar Pedidos

```powershell
Invoke-WebRequest -Uri "http://localhost:8000/pedidos/api/pedidos" `
    -Method GET `
    -Headers @{"Authorization" = "Bearer $token"} `
    -UseBasicParsing | Select-Object -ExpandProperty Content
```

---

## 🔍 Comandos Útiles de Diagnóstico

### Ver todos los pods
```powershell
kubectl get pods -n logiflow
```

### Ver logs de un servicio
```powershell
# Auth service
kubectl logs -n logiflow -l app=auth-service --tail=100

# Pedido service
kubectl logs -n logiflow -l app=pedido-service --tail=100

# Kong Gateway
kubectl logs -n logiflow -l app=kong-gateway --tail=100
```

### Ver secretos
```powershell
# Listar todos los secretos
kubectl get secrets -n logiflow

# Ver contenido del secret app-secrets
kubectl get secret app-secrets -n logiflow -o yaml
```

### Verificar conectividad de base de datos
```powershell
# Conectar y ejecutar query
kubectl exec -n logiflow deployment/postgres-auth -- psql -U logiflow -d auth_db -c "SELECT username, email FROM usuarios;"
```

### Ver servicios y sus puertos
```powershell
kubectl get svc -n logiflow
```

### Reiniciar un servicio
```powershell
kubectl rollout restart deployment/pedido-service -n logiflow
```

---

## 📋 Checklist de Verificación

- [x] Todos los pods están en estado Running (13/13)
- [x] Kong Gateway accesible en puerto 8000
- [x] Auth service: registro y login funcionando
- [x] Pedido service: creación de pedidos funcionando
- [x] Base de datos auth_db con 2 usuarios de prueba
- [x] JWT tokens generándose con HS512 (256-bit secret)
- [x] RabbitMQ accesible (AMQP + Management UI)
- [x] Ingress NGINX controller activo
- [ ] Fleet service endpoints probados
- [ ] Billing service endpoints probados
- [ ] GraphQL gateway probado
- [ ] Minikube tunnel configurado para Ingress

---

## 🐛 Bugs Resueltos

### Auth Service (4 bugs corregidos)
1. ✅ **PostgreSQL DEFAULT en usuario_roles**: Columna `usuario_id` tenía `DEFAULT gen_random_uuid()` causando UUIDs aleatorios en FK
2. ✅ **NullPointerException en generateAuthResponse()**: Faltaba null check antes de `usuario.getRoles().stream()`
3. ✅ **JWT Secret débil**: Secret original de 120 bits, actualizado a 256 bits
4. ✅ **Roles initialization timing**: Roles se seteaban después del primer `save()`, movido antes

### Pedido Service (1 bug corregido)
1. ✅ **Campo zonaId no mapeado**: El método `crearPedido()` no copiaba `request.getZonaId()` a la entidad Pedido, causando constraint violation en PostgreSQL

---

## 📞 Soporte

Para problemas con la configuración:
1. Verificar que Minikube esté corriendo: `minikube status`
2. Verificar namespace: `kubectl config set-context --current --namespace=logiflow`
3. Verificar logs de los pods con problemas
4. Verificar conectividad de red: `kubectl get svc -n logiflow`

---

**Última actualización**: 2026-02-05 00:02 UTC  
**Documentado por**: GitHub Copilot  
**Estado del sistema**: ✅ Operacional
