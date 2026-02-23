# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./
COPY tsconfig.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY src ./src

# Compilar TypeScript
RUN npm run build

# Stage 2: Production
FROM node:20-alpine

WORKDIR /app

# Crear usuario no privilegiado
RUN addgroup -g 1001 -S nodejs && \
    adduser -S graphql -u 1001

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar solo dependencias de producción
RUN npm ci --only=production && \
    npm cache clean --force

# Copiar build desde stage anterior
COPY --from=builder /app/dist ./dist

# Cambiar ownership de los archivos
RUN chown -R graphql:nodejs /app

# Cambiar a usuario no privilegiado
USER graphql

# Exponer puerto GraphQL
EXPOSE 4000

# Variables de entorno por defecto (pueden sobreescribirse en docker-compose)
ENV NODE_ENV=production \
    PORT=4000 \
    GRAPHQL_PATH=/graphql \
    PLAYGROUND_ENABLED=true

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD node -e "require('http').get('http://localhost:4000/', (r) => {if (r.statusCode === 200) process.exit(0); else {console.log(r.statusCode); process.exit(1)}}).on('error', (err) => process.exit(1))"

# Comando de inicio
CMD ["node", "dist/index.js"]
