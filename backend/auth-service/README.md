# Auth Service - LogiFlow

Servicio de autenticación y autorización basado en JWT para la plataforma LogiFlow de EntregaExpress S.A.

## Descripción

Auth Service es el microservicio encargado de:
- Registro de nuevos usuarios
- Autenticación mediante JWT (JSON Web Tokens)
- Generación y renovación de tokens de acceso y refresh
- Gestión de roles de usuario
- Control de intentos fallidos de login y bloqueo de cuentas

## Tecnologías

- **Spring Boot 4.0.0** - Framework principal
- **Java 21** - Lenguaje de programación
- **PostgreSQL 16** - Base de datos relacional
- **Spring Security** - Seguridad y autenticación
- **JWT (jjwt 0.12.6)** - Tokens de autenticación
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## Arquitectura

```
auth-service/
├── model/                 # Entidades JPA
│   ├── Usuario.java
│   └── RolEnum.java
├── dto/                   # Data Transfer Objects
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   └── RefreshTokenRequest.java
├── repository/            # Repositorios JPA
│   └── UsuarioRepository.java
├── service/               # Lógica de negocio
│   └── AuthService.java
├── controller/            # Controladores REST
│   ├── AuthController.java
│   └── HomeController.java
├── config/                # Configuración
│   └── SecurityConfig.java
└── util/                  # Utilidades
    └── JwtUtil.java
```

## Configuración

### Variables de Entorno

```yaml
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/db_logiflow_auth
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=root

# Servidor
SERVER_PORT=8082

# JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000          # 24 horas
JWT_REFRESH_EXPIRATION=604800000  # 7 días
```

### Base de Datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE db_logiflow_auth;
```

Las tablas se crean automáticamente con `ddl-auto: update`.

Cargar datos de prueba:
```sql
psql -U postgres -d db_logiflow_auth -f src/main/resources/test-data.sql
```

## Endpoints

### GET /
Información del servicio

### POST /api/auth/register
Registro de nuevos usuarios

**Request:**
```json
{
  "username": "nuevoCliente",
  "email": "nuevo@example.com",
  "password": "password123",
  "nombreCompleto": "Nuevo Cliente",
  "telefono": "0991234567"
}
```

**Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "nuevoCliente",
  "email": "nuevo@example.com",
  "roles": ["CLIENTE"]
}
```

### POST /api/auth/login
Autenticación de usuarios

**Request:**
```json
{
  "username": "cliente1",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "cliente1",
  "email": "cliente1@logiflow.com",
  "roles": ["CLIENTE"]
}
```

### POST /api/auth/refresh
Renovar access token

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "username": "cliente1",
  "email": "cliente1@logiflow.com",
  "roles": ["CLIENTE"]
}
```

## Roles del Sistema

| Rol | Descripción |
|-----|-------------|
| `CLIENTE` | Clientes que solicitan entregas (asignado por defecto en registro) |
| `REPARTIDOR` | Repartidores (motorizado, vehículo liviano, camión) |
| `SUPERVISOR` | Supervisores de operaciones |
| `GERENTE` | Gerentes de la empresa |
| `ADMINISTRADOR` | Administradores del sistema |

## Seguridad

### JWT Claims
Los tokens JWT incluyen los siguientes claims:
- `sub`: username del usuario
- `roles`: lista de roles asignados
- `email`: email del usuario
- `nombreCompleto`: nombre completo
- `iat`: fecha de emisión
- `exp`: fecha de expiración

### Protección de Cuenta
- Máximo 3 intentos fallidos de login
- Bloqueo automático de cuenta después de 3 intentos
- Registro de fecha de último acceso

### Encriptación
- Passwords encriptados con **BCrypt**
- Tokens firmados con **HMAC-SHA256**

## Ejecución

### Desarrollo Local

```bash
# Compilar
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run

# Con variables de entorno
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/db_logiflow_auth
./mvnw spring-boot:run
```

### Docker

```bash
# Build
docker build -t auth-service:latest .

# Run
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-auth:5432/db_logiflow_auth \
  auth-service:latest
```

### Docker Compose

Ver el archivo `docker-compose.yml` en la raíz del proyecto.

## Pruebas

### Con cURL

```bash
# Registro
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123","nombreCompleto":"Test User"}'

# Login
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"cliente1","password":"password123"}'

# Refresh
curl -X POST http://localhost:8082/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN_HERE"}'
```

### Con Postman

Ver `POSTMAN_EXAMPLES.txt` para ejemplos completos.

## Usuarios de Prueba

Usuarios disponibles después de ejecutar `test-data.sql`:

| Username | Password | Rol(es) |
|----------|----------|---------|
| cliente1 | password123 | CLIENTE |
| repartidor1 | password123 | REPARTIDOR |
| supervisor1 | password123 | SUPERVISOR |
| gerente1 | password123 | GERENTE |
| admin | password123 | ADMINISTRADOR |
| multi1 | password123 | SUPERVISOR, REPARTIDOR |

## Integración con Otros Servicios

Para consumir endpoints protegidos en otros microservicios:

```http
GET http://localhost:8083/api/pedidos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Los otros servicios deben validar el token usando la misma clave secreta (`JWT_SECRET`).

## Logs

Los logs están configurados en nivel `DEBUG` para:
- `ec.edu.espe.*`
- `org.springframework.security.*`
- `org.springframework.web.*`
- `org.hibernate.SQL`

## Troubleshooting

### Error: "Username ya existe"
El username debe ser único. Intenta con otro.

### Error: "Cuenta bloqueada"
La cuenta se bloqueó por múltiples intentos fallidos. Contacta al administrador o modifica directamente en BD.

### Error: "Token inválido o expirado"
- El access token expira en 24 horas
- El refresh token expira en 7 días
- Usa el refresh token para obtener un nuevo access token
- Si ambos expiraron, haz login nuevamente

### Error de conexión a PostgreSQL
Verifica que PostgreSQL esté corriendo en el puerto 5434 y que la base de datos `db_logiflow_auth` exista.

## Licencia

Proyecto académico - ESPE 2025

## Autor

Equipo de desarrollo LogiFlow - EntregaExpress S.A.
