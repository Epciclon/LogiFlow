# LogiFlow - Sistema Completo de Gestión de Delivery

## 📋 Descripción General
LogiFlow es una plataforma completa de microservicios para gestión de operaciones de delivery multinivel:
- **Urbana Rápida**: ≤ 20 km
- **Intermunicipal**: ≤ 150 km  
- **Nacional**: Sin límite de distancia

## 🏗️ Arquitectura Actual (Fase 2A - Completada)

```
┌──────────────── KUBERNETES CLUSTER (Minikube) ────────────────┐
│                                                                 │
│   ┌─────────────────────────────────────────────────────┐     │
│   │        KONG API GATEWAY (Puerto 8000)               │     │
│   │  /auth → /pedidos → /fleet → /billing → /graphql   │     │
│   │             /notifications (WebSocket + HTTP)       │     │
│   └────────────────────┬────────────────────────────────┘     │
│                        │                                       │
│   ┌────────────────────┴───────────────────────────────┐      │
│   │                                                     │      │
│   ├─ auth-service:8082        ├─ notification-service:8087   │
│   ├─ pedido-service:8083      │   ├─ RabbitMQ Consumer      │
│   ├─ fleet-service:8084       │   ├─ WebSocket Gateway      │
│   ├─ billing-service:8085     │   └─ PostgreSQL DB          │
│   ├─ graphql-gateway:4000     │                               │
│   │                            │                               │
│   └────────────┬───────────────┘                               │
│                │                                                │
│   ┌────────────▼─────────────┐                                │
│   │  RabbitMQ (Exchange)     │  ← Events desde microservicios │
│   │  notifications_exchange  │                                 │
│   └──────────────────────────┘                                │
│                                                                 │
│   PostgreSQL Databases: auth, pedidos, fleet, billing, notif  │
└─────────────────────────────────────────────────────────────────┘
```

## 🎯 Servicios Implementados

| Servicio | Puerto | Kubernetes | Descripción |
|----------|--------|------------|-------------|
| **Kong Gateway** | 8000 | ✅ | API Gateway principal |
| **Auth Service** | 8082 | ✅ | Autenticación JWT |
| **Pedido Service** | 8083 | ✅ | Gestión de pedidos + Events |
| **Fleet Service** | 8084 | ✅ | Gestión de flota + Events |
| **Billing Service** | 8085 | ✅ | Facturación |
| **Notification Service** | 8087 | ✅ | WebSocket + RabbitMQ Consumer |
| **GraphQL Gateway** | 4000 | ✅ | API GraphQL Unificada |
| **RabbitMQ** | 5672/15672 | ✅ | Message Broker |

## 🚀 Inicio Rápido con Kubernetes

### Prerequisitos
- Minikube instalado
- kubectl configurado
- Docker Desktop
- 8 GB RAM mínimo

### 1. Iniciar Kubernetes Cluster

```powershell
# Navegar al directorio
cd c:\AppServ\Distribuidas\delivery

# Iniciar Minikube
minikube start --cpus=4 --memory=8192

# Verificar
kubectl cluster-info
```

### 2. Desplegar Toda la Plataforma

```powershell
# Aplicar todos los manifiestos en orden
kubectl apply -f kubernetes/00-namespace.yaml
kubectl apply -f kubernetes/01-configmap.yaml
kubectl apply -f kubernetes/02-secrets.yaml
kubectl apply -f kubernetes/03-postgres-auth.yaml
kubectl apply -f kubernetes/04-postgres-pedidos.yaml
kubectl apply -f kubernetes/05-postgres-fleet.yaml
kubectl apply -f kubernetes/06-postgres-billing.yaml
kubectl apply -f kubernetes/07-postgres-notifications.yaml
kubectl apply -f kubernetes/08-rabbitmq.yaml
kubectl apply -f kubernetes/09-auth-service.yaml
kubectl apply -f kubernetes/10-pedido-service.yaml
kubectl apply -f kubernetes/11-fleet-service.yaml
kubectl apply -f kubernetes/12-billing-service.yaml
kubectl apply -f kubernetes/13-notification-service.yaml
kubectl apply -f kubernetes/14-graphql-gateway.yaml
kubectl apply -f kubernetes/15-kong-gateway.yaml

# Verificar que todos los pods estén corriendo
kubectl get pods -n logiflow
```

### 3. Exponer Kong Gateway

```powershell
# Terminal separado (dejar corriendo)
kubectl port-forward -n logiflow svc/kong-gateway 8000:8000
```

## 📡 Fase 2A - Notificaciones en Tiempo Real

### ✅ Características Implementadas

#### 1. Productores de Eventos (RabbitMQ)
- **pedido-service**: Publica eventos de creación, actualización y cancelación de pedidos
- **fleet-service**: Publica eventos de repartidores y vehículos
- **Exchange**: `notifications_exchange` (tipo: topic)
- **Queue**: `notifications_queue`

#### 2. Consumidor de Eventos
- **notification-service**: Consume eventos de RabbitMQ
- Almacena notificaciones en PostgreSQL
- Broadcast automático vía WebSocket a clientes suscritos

#### 3. WebSocket Server
- **Endpoint**: `ws://localhost:8000/notifications/socket.io/` (a través de Kong)
- **Autenticación JWT**: Token requerido en query param o Authorization header
- **Suscripciones selectivas**:
  - `all` - Todos los eventos (solo SUPERVISORES)
  - `pedido:{id}` - Eventos de un pedido específico (CLIENTES)
  - `repartidor:{id}` - Eventos de un repartidor (REPARTIDORES)

#### 4. Sistema de Replay
- Cache en memoria: últimos 50 eventos (TTL 5 minutos)
- **Replay inteligente**: Solo envía eventos relevantes según suscripción
- Ejemplo: Si te suscribes a `pedido:123`, solo recibes replay de eventos de ese pedido

### 🧪 Probar WebSocket

1. Abrir `backend/notification-service/websocket-client-test.html` en navegador
2. Configurar URL: `http://localhost:8000`
3. Obtener token JWT del auth-service
4. Conectar al WebSocket
5. Suscribirse a un canal:
   - `all` para ver todos los eventos
   - `pedido:{uuid}` para un pedido específico
   - `repartidor:{uuid}` para un repartidor específico

## 🔧 Tecnologías Utilizadas

| Componente | Tecnología | Versión |
|------------|------------|---------|
| **Orquestación** | Kubernetes (Minikube) | 1.37+ |
| **API Gateway** | Kong | 3.4 |
| **Backend Services** | Spring Boot | 4.0.0 |
| **Lenguaje** | Java | 21 |
| **Base de Datos** | PostgreSQL | 16 |
| **Message Broker** | RabbitMQ | 3.13 |
| **WebSocket Service** | NestJS + Socket.IO | 10 / 4.7 |
| **API GraphQL** | Apollo Server + Express | 4 |
| **Autenticación** | Spring Security + JWT | jjwt 0.12.6 |
| **Containerización** | Docker | Latest |

## 📊 Estado del Proyecto

### ✅ Fase 1: Microservicios Base
- [x] Auth Service con JWT
- [x] Pedido Service con validación geográfica
- [x] Fleet Service
- [x] Billing Service con tarifas dinámicas
- [x] Kong API Gateway
- [x] Docker Compose completo

### ✅ Fase 2A: Notificaciones y Eventos
- [x] RabbitMQ configurado
- [x] Productores de eventos en pedido-service
- [x] Productores de eventos en fleet-service
- [x] Notification-service con Consumer
- [x] WebSocket Gateway con autenticación JWT
- [x] Sistema de replay con filtros por suscripción
- [x] Almacenamiento de notificaciones en PostgreSQL

### ✅ Fase 2B: Kubernetes Deployment
- [x] Migración completa a Kubernetes
- [x] 13 pods desplegados (12 servicios + Kong Gateway)
- [x] ConfigMaps y Secrets configurados
- [x] Health checks y resource limits
- [x] Kong Gateway como punto único de entrada
- [x] GraphQL Gateway integrado con Kong

### 🔜 Fase 3: Próximas Características
- [ ] Frontend React con subscripciones WebSocket
- [ ] Dashboard en tiempo real para supervisores
- [ ] Métricas y monitoreo con Prometheus
- [ ] Escalado automático (HPA)
- [ ] CI/CD con GitHub Actions

## 📚 Documentación Completa

| Documento | Descripción |
|-----------|-------------|
| [README.md](README.md) | Este archivo |
| [INICIO_RAPIDO.md](INICIO_RAPIDO.md) | Guía completa de pruebas |
| [GUIA_ACCESO_COMPLETA.md](kubernetes/GUIA_ACCESO_COMPLETA.md) | Kong, Ingress, Port-Forward |
| [CREDENCIALES_COMPLETAS.md](kubernetes/CREDENCIALES_COMPLETAS.md) | Usuarios y credenciales |
| [FASE_2A_NOTIFICACIONES_RESUMEN.md](FASE_2A_NOTIFICACIONES_RESUMEN.md) | Detalle de notificaciones |
| [backend/notification-service/README_LOGIFLOW.md](backend/notification-service/README_LOGIFLOW.md) | WebSocket API |

## 🎯 Comandos Útiles

### Kubernetes
```powershell
# Ver todos los pods
kubectl get pods -n logiflow

# Ver logs de un servicio
kubectl logs -n logiflow -l app=notification-service --tail=50

# Reiniciar un deployment
kubectl rollout restart deployment/notification-service -n logiflow

# Port-forward a un servicio
kubectl port-forward -n logiflow svc/kong-gateway 8000:8000
```

### Verificar Servicios
```powershell
# Login
curl -X POST http://localhost:8000/auth/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username": "admin", "password": "password123"}'

# Listar pedidos
curl http://localhost:8000/pedidos/api/pedidos `
  -H "Authorization: Bearer <token>"

# Listar notificaciones
curl http://localhost:8000/notifications/notifications `
  -H "Authorization: Bearer <token>"
```

## 👥 Usuarios de Prueba

| Username | Password | Rol | Uso |
|----------|----------|-----|-----|
| admin | password123 | ADMINISTRADOR | Acceso completo |
| supervisor1 | password123 | SUPERVISOR | WebSocket canal "all" |
| repartidor1 | password123 | REPARTIDOR | Canal repartidor:{id} |
| cliente1 | password123 | CLIENTE | Canal pedido:{id} |

## 🔒 Seguridad WebSocket

- **Autenticación JWT**: Requerida en producción
- **Suscripciones por rol**:
  - SUPERVISOR puede suscribirse a "all"
  - CLIENTE solo puede ver sus propios pedidos
  - REPARTIDOR solo puede ver sus asignaciones
- **Replay filtrado**: Solo eventos relevantes según suscripción

## 📞 Soporte y Contribuciones

Este proyecto es parte del sistema LogiFlow para gestión de delivery multinivel.

**Estado**: ✅ Fase 2A Completada - WebSocket con Replay Inteligente  
**Última actualización**: Febrero 2026
