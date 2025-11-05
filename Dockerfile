# ===================================================================
# STAGE 1: BUILD - Compilar la aplicación con Maven y Java 21
# ===================================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (capa de cache)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar con perfil de producción (Java 21 target)
RUN mvn clean package -Pproduction -DskipTests

# ===================================================================
# STAGE 2: RUNTIME - Imagen de producción optimizada
# ===================================================================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Crear usuario no-root para seguridad
RUN groupadd -r spring && useradd -r -g spring spring

# Copiar JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar ownership del directorio
RUN chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring

# Exponer puerto (Render asigna dinámicamente via $PORT)
EXPOSE 8080

# Variables de entorno por defecto (Render las sobrescribirá)
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Comando de inicio con variables de entorno
# Render pasa PORT, AIVEN_DATABASE_URL, AIVEN_DB_USERNAME, AIVEN_DB_PASSWORD, etc.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-production} -jar app.jar"]
