# 🚀 Guía Completa de Acceso - LogiFlow Kubernetes

## 📋 Índice
1. [Arquitectura de Acceso](#arquitectura)
2. [Kong API Gateway - Puerto 8000](#kong-gateway)
3. [Ingress Controller - networking.k8s.io](#ingress)
4. [Port-Forward Directo](#port-forward)
5. [Ejemplos de Uso](#ejemplos)

---

## 🏗️ Arquitectura de Acceso {#arquitectura}

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE EXTERNO                          │
└────────────┬────────────────────────────────────────────────┘
             │
             ├─── Opción 1: Ingress (networking.k8s.io/v1)
             │    └─> http://api.logiflow.local → Kong Gateway
             │    └─> http://graphql.logiflow.local → GraphQL
             │
             ├─── Opción 2: NodePort 
             │    └─> minikube_ip:30800 → Kong Gateway
             │    └─> minikube_ip:30400 → GraphQL
             │
             └─── Opción 3: Port-Forward
                  └─> kubectl port-forward ...
                  
┌─────────────────────────────────────────────────────────────┐
│  KONG API GATEWAY (Puerto 8000)                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ /auth      → auth-service:8082                       │   │
│  │ /pedidos   → pedido-service:8083                     │   │
│  │ /fleet     → fleet-service:8084                      │   │
│  │ /billing   → billing-service:8085                    │   │
│  │ /graphql   → graphql-gateway:4000                    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🦍 Kong API Gateway - Puerto 8000 {#kong-gateway}

Kong es el **punto de entrada único** para todas las APIs REST. Proporciona:
- ✅ Rate limiting (100 requests/minuto)
- ✅ CORS configurado
- ✅ Ruteo centralizado
- ✅ Logs y métricas

### 🔌 Acceso a Kong

#### Método 1: Port-Forward (Desarrollo)
```bash
# Gateway Proxy (APIs)
kubectl port-forward -n logiflow svc/kong-gateway 8000:8000

# Admin API (Gestión)
kubectl port-forward -n logiflow svc/kong-gateway 8001:8001
```

#### Método 2: NodePort (Acceso desde Host)
```bash
# Obtener IP de Minikube
minikube ip
# Ejemplo: 192.168.49.2

# Acceder a:
# - APIs: http://192.168.49.2:30800
# - Admin: http://192.168.49.2:30801
```

#### Método 3: Ingress (Producción)
```bash
# Configurar hosts
# Windows: C:\Windows\System32\drivers\etc\hosts
# Linux/Mac: /etc/hosts

# Agregar:
192.168.49.2  api.logiflow.local

# Acceder:
http://api.logiflow.local
```

### 📡 Rutas de Kong

| Ruta | Servicio Destino | Descripción |
|------|------------------|-------------|
| `POST /auth/register` | auth-service:8082 | Registro de usuarios |
| `POST /auth/login` | auth-service:8082 | Login y obtener JWT |
| `GET /pedidos` | pedido-service:8083 | Listar pedidos |
| `POST /pedidos` | pedido-service:8083 | Crear pedido |
| `GET /fleet/drivers` | fleet-service:8084 | Listar conductores |
| `GET /billing/invoices` | billing-service:8085 | Listar facturas |
| `POST /graphql` | graphql-gateway:4000 | Queries GraphQL |
| `GET /notifications/notifications` | notification-service:8087 | Listar notificaciones |
| `WS /notifications` | notification-service:8087 | WebSocket tiempo real |

### 🧪 Ejemplo de Uso Kong

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario@example.com",
    "password": "Pass123!",
    "fullName": "Usuario Test"
  }'

# 2. Login
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario@example.com",
    "password": "Pass123!"
  }'

# Respuesta incluye: { "token": "eyJhbG..." }

# 3. Crear pedido (con token)
curl -X POST http://localhost:8000/pedidos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbG..." \
  -d '{
    "origen": "Calle 1",
    "destino": "Calle 2",
    "peso": 5.5
  }'
```

---

## 🌐 Ingress - networking.k8s.io/v1 {#ingress}

Ingress es el recurso de Kubernetes para exponer servicios HTTP/HTTPS.

### 📦 Componentes

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
```

**Versiones de API:**
- `networking.k8s.io/v1` - ✅ Actual (K8s 1.19+)
- `networking.k8s.io/v1beta1` - ⚠️ Deprecado
- `extensions/v1beta1` - ❌ Removido

### 🎯 Recursos Configurados

#### Ingress Principal
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: logiflow-ingress
  namespace: logiflow
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
spec:
  ingressClassName: nginx
  rules:
  - host: api.logiflow.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: kong-gateway
            port:
              number: 8000
```

### 🔧 Configuración del Host

**Windows:**
```powershell
# Editar como Administrador
notepad C:\Windows\System32\drivers\etc\hosts

# Agregar:
192.168.49.2  api.logiflow.local
192.168.49.2  graphql.logiflow.local
192.168.49.2  rabbitmq.logiflow.local
```

**Linux/Mac:**
```bash
sudo nano /etc/hosts

# Agregar:
192.168.49.2  api.logiflow.local
192.168.49.2  graphql.logiflow.local
192.168.49.2  rabbitmq.logiflow.local
```

### 🚦 Iniciar Túnel de Minikube

```bash
# Terminal separado (dejar corriendo)
minikube tunnel

# Ahora accesible en:
# - http://api.logiflow.local
# - http://graphql.logiflow.local
# - http://rabbitmq.logiflow.local
```

### 📊 Verificar Ingress

```bash
# Ver Ingress
kubectl get ingress -n logiflow

# Detalles
kubectl describe ingress logiflow-ingress -n logiflow

# Ver controller
kubectl get pods -n ingress-nginx

# Logs del controller
kubectl logs -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx
```

---

## 🔌 Port-Forward Directo {#port-forward}

Para acceso directo sin Gateway ni Ingress:

```bash
# Auth Service
kubectl port-forward -n logiflow svc/auth-service 8082:8082

# Pedido Service  
kubectl port-forward -n logiflow svc/pedido-service 8083:8083

# Fleet Service
kubectl port-forward -n logiflow svc/fleet-service 8084:8084

# Billing Service
kubectl port-forward -n logiflow svc/billing-service 8085:8085

# Notification Service
kubectl port-forward -n logiflow svc/notification-service 8087:8087

# GraphQL Gateway
kubectl port-forward -n logiflow svc/graphql-gateway 4000:4000

# RabbitMQ Management
kubectl port-forward -n logiflow svc/rabbitmq 15672:15672

# Kong Gateway
kubectl port-forward -n logiflow svc/kong-gateway 8000:8000

# Kong Admin API
kubectl port-forward -n logiflow svc/kong-gateway 8001:8001
```

---

## 🧪 Ejemplos de Uso Completos {#ejemplos}

### 1️⃣ Flujo Completo con Kong Gateway

```bash
# Iniciar port-forward de Kong
kubectl port-forward -n logiflow svc/kong-gateway 8000:8000

# Terminal 2: Ejecutar requests
# 1. Registrar usuario
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "conductor@mail.com",
    "password": "Pass123!",
    "fullName": "Juan Conductor",
    "role": "DRIVER"
  }'

# 2. Login
TOKEN=$(curl -s -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "conductor@mail.com",
    "password": "Pass123!"
  }' | jq -r '.token')

echo "Token: $TOKEN"

# 3. Crear pedido
curl -X POST http://localhost:8000/pedidos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "direccionOrigen": "Av. Amazonas N21-45",
    "direccionDestino": "Av. 6 de Diciembre N35-120",
    "peso": 12.5,
    "descripcion": "Paquete urgente"
  }'

# 4. Listar pedidos
curl -X GET http://localhost:8000/pedidos \
  -H "Authorization: Bearer $TOKEN"

# 5. Ver conductores disponibles
curl -X GET http://localhost:8000/fleet/drivers \
  -H "Authorization: Bearer $TOKEN"
```

### 2️⃣ Consultas GraphQL

```bash
# Port-forward GraphQL
kubectl port-forward -n logiflow svc/graphql-gateway 4000:4000

# Query GraphQL
curl -X POST http://localhost:4000/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "query": "{ pedidos { id origen destino estado } }"
  }'

# Mutation GraphQL
curl -X POST http://localhost:4000/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "query": "mutation { crearPedido(input: { origen: \"Calle A\", destino: \"Calle B\", peso: 10 }) { id estado } }"
  }'
```

### 3️⃣ Usar Ingress (con minikube tunnel)

```bash
# Terminal 1: Iniciar tunnel
minikube tunnel

# Terminal 2: Requests via Ingress
# 1. Kong API Gateway
curl http://api.logiflow.local/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user@mail.com", "password": "Pass123!"}'

# 2. GraphQL directo
curl -X POST http://graphql.logiflow.local/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"query": "{ pedidos { id } }"}'

# 3. RabbitMQ Management
# Abrir en navegador: http://rabbitmq.logiflow.local
```

### 4️⃣ Verificar Kong Admin API

```bash
# Port-forward Admin API
kubectl port-forward -n logiflow svc/kong-gateway 8001:8001

# Ver servicios configurados
curl http://localhost:8001/services

# Ver rutas
curl http://localhost:8001/routes

# Ver plugins activos
curl http://localhost:8001/plugins

# Health check
curl http://localhost:8001/status
```

---

## 📊 Resumen de Puertos

| Servicio | Puerto Interno | NodePort | Ingress Host |
|----------|----------------|----------|--------------|
| Kong Gateway | 8000 | 30800 | api.logiflow.local |
| Kong Admin | 8001 | 30801 | - |
| GraphQL Gateway | 4000 | 30400 | graphql.logiflow.local |
| RabbitMQ Management | 15672 | 30672 | rabbitmq.logiflow.local |
| Auth Service | 8082 | - | - |
| Pedido Service | 8083 | - | - |
| Fleet Service | 8084 | - | - |
| Billing Service | 8085 | - | - |
| Notification Service | 8087 | - | - |

---

## 🔐 Seguridad y Autenticación

### JWT Token Flow
1. **Register/Login** → Obtener token JWT
2. **Incluir en headers**: `Authorization: Bearer <token>`
3. **Kong valida** (si se configura plugin JWT)
4. **Servicios verifican** token internamente

### Plugins de Kong (Opcional)

Para agregar autenticación JWT en Kong, editar `12-kong-gateway.yaml`:

```yaml
plugins:
- name: jwt
  config:
    key_claim_name: iss
    secret_is_base64: false
```

---

## 🛠️ Troubleshooting

### Ingress no funciona
```bash
# Verificar addon
minikube addons list | grep ingress

# Ver logs del controller
kubectl logs -n ingress-nginx -l app.kubernetes.io/component=controller

# Verificar tunnel
minikube tunnel --cleanup
```

### Kong no responde
```bash
# Ver logs
kubectl logs -n logiflow -l app=kong-gateway

# Verificar config
kubectl get configmap kong-declarative-config -n logiflow -o yaml

# Restart
kubectl rollout restart deployment kong-gateway -n logiflow
```

### Rate limit alcanzado
```bash
# Verificar en logs de Kong
kubectl logs -n logiflow -l app=kong-gateway | grep "rate-limit"

# Modificar límite en kong.yml
# Cambiar "minute: 100" a valor mayor
```

---

## 🎯 Mejores Prácticas

1. **Desarrollo**: Usar `kubectl port-forward`
2. **Testing Local**: Usar Ingress + `minikube tunnel`
3. **CI/CD**: Usar NodePort para tests automatizados
4. **Producción**: Configurar Ingress con TLS/SSL

---

**Estado:** ✅ Kong Gateway Funcionando  
**Ingress:** ✅ Habilitado con NGINX Controller  
**Acceso:** 3 métodos disponibles (Port-Forward, NodePort, Ingress)
