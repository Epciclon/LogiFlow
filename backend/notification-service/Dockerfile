# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Compilar aplicación NestJS
RUN npm run build

# Stage 2: Production
FROM node:20-alpine

WORKDIR /app

# Crear usuario no privilegiado
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nestjs -u 1001

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar solo dependencias de producción
RUN npm ci --only=production && \
    npm cache clean --force

# Copiar build desde stage anterior
COPY --from=builder /app/dist ./dist

# Cambiar ownership de los archivos
RUN chown -R nestjs:nodejs /app

# Cambiar a usuario no privilegiado
USER nestjs

# Exponer puerto
EXPOSE 3001

# Variables de entorno por defecto (pueden sobreescribirse en docker-compose)
ENV NODE_ENV=production \
    SERVER=3001 \
    RABBITMQ_HOST=rabbitmq \
    RABBITMQ_PORT=5672 \
    RABBITMQ_USERNAME=admin \
    RABBITMQ_PASSWORD=admin \
    DB_HOST=postgres-notifications \
    DB_PORT=5432 \
    DB_USER=postgres \
    DB_PASSWORD=root \
    DB_NAME=db_notifications

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD node -e "require('http').get('http://localhost:3001/health', (r) => {process.exit(r.statusCode === 200 ? 0 : 1)})"

# Comando de inicio
CMD ["node", "dist/main.js"]
