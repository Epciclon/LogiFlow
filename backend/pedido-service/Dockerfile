# ================================================
# Dockerfile - Pedido Service - LogiFlow
# Multi-stage build para optimizar el tamaño de la imagen
# ================================================

# Etapa 1: Build con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar solo pom.xml primero para aprovechar cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto del servicio
EXPOSE 8083

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
