# Sistema de Gestión de Eventos PMI

## Descripción General

API REST para la gestión integral de eventos del Project Management Institute (PMI). Este sistema backend proporciona funcionalidades completas para la administración, registro y seguimiento de eventos, incluyendo generación de códigos QR, exportación de datos y notificaciones por correo electrónico.

## Tecnologías Utilizadas

- **Java 21** (compatibilidad con Java 25 para desarrollo local)
- **Spring Boot 3.5.6** - Framework principal para desarrollo de aplicaciones
- **Spring Data JPA** - Persistencia y acceso a datos
- **MySQL** - Base de datos relacional
- **Maven** - Gestión de dependencias y construcción del proyecto
- **Docker** - Conteneurización de la aplicación
- **Apache POI** - Procesamiento de archivos Excel
- **OpenCSV** - Manejo de archivos CSV
- **ZXing** - Generación de códigos QR
- **iCal4j** - Gestión de eventos de calendario
- **Spring Boot Mail** - Envío de notificaciones por correo
- **Lombok** - Reducción de código boilerplate

## Requisitos Previos

- Java Development Kit (JDK) 21 o superior
- Maven 3.6 o superior
- MySQL 8.0 o superior
- Docker (opcional, para conteneurización)

## Instalación

### Clonación del Repositorio

```bash
git clone https://github.com/Davs07/EventsManagement-PMI.git
cd EventsManagement-PMI
```

### Configuración de la Base de Datos

1. Crear una base de datos MySQL:
```sql
CREATE DATABASE eventos_pmi;
```

2. Configurar las credenciales de acceso en `src/main/resources/application.properties`

## Configuración del Entorno

### Variables de Entorno Requeridas

```properties
# Configuración de Base de Datos
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/eventos_pmi
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_contraseña

# Configuración de Correo Electrónico
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu_email@gmail.com
SPRING_MAIL_PASSWORD=tu_contraseña_app

# Configuración de la Aplicación
SERVER_PORT=8080
```

### Archivo de Configuración

Crear o modificar `src/main/resources/application.properties`:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.mail.host=${SPRING_MAIL_HOST}
spring.mail.port=${SPRING_MAIL_PORT}
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

server.port=${SERVER_PORT:8080}
```

## Comandos de Ejecución

### Desarrollo Local con Maven

```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar pruebas
./mvnw test

# Construir el JAR
./mvnw clean package

# Ejecutar la aplicación
./mvnw spring-boot:run

# Ejecutar con perfil de desarrollo (Java 25)
./mvnw spring-boot:run -Pdev
```

### Ejecución con Docker

```bash
# Construir la imagen Docker
docker build -t eventos-pmi-api .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/eventos_pmi \
  -e SPRING_DATASOURCE_USERNAME=tu_usuario \
  -e SPRING_DATASOURCE_PASSWORD=tu_contraseña \
  eventos-pmi-api
```

### Ejecución del JAR

```bash
# Después de construir con Maven
java -jar target/api.gestion.eventos-0.0.1-SNAPSHOT.jar
```

## Estructura del Proyecto

```
EventsManagement-PMI/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/api/gestion/eventos/
│   │   │       ├── controller/     # Controladores REST
│   │   │       ├── service/        # Lógica de negocio
│   │   │       ├── repository/     # Acceso a datos
│   │   │       ├── model/          # Entidades JPA
│   │   │       ├── dto/            # Objetos de transferencia de datos
│   │   │       └── config/         # Configuraciones
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/                       # Pruebas unitarias e integración
├── uploads/                        # Directorio para archivos subidos
├── Dockerfile                      # Configuración Docker
├── pom.xml                        # Configuración Maven
├── render.yaml                    # Configuración para despliegue
└── README.md
```

## Guía de Contribución

### Flujo de Trabajo con Git

1. **Crear una nueva rama** para cada funcionalidad o corrección:
```bash
git checkout -b feature/nueva-funcionalidad
```

2. **Realizar cambios** siguiendo las convenciones del proyecto:
   - Usar nombres descriptivos para métodos y variables
   - Agregar comentarios JavaDoc para métodos públicos
   - Mantener la consistencia en el estilo de código

3. **Realizar commits** con mensajes claros y descriptivos:
```bash
git add .
git commit -m "feat: agregar funcionalidad de exportación de eventos"
```

4. **Enviar cambios** al repositorio remoto:
```bash
git push origin feature/nueva-funcionalidad
```

5. **Crear Pull Request** con:
   - Título descriptivo del cambio
   - Descripción detallada de las modificaciones
   - Referencia a issues relacionados si aplica

### Estándares de Código

- Seguir las convenciones de Java y Spring Boot
- Utilizar anotaciones de Lombok para reducir código repetitivo
- Implementar pruebas unitarias para nueva funcionalidad
- Mantener la documentación actualizada

### Revisión de Código

- Todo código debe ser revisado antes de ser integrado
- Las pruebas deben pasar exitosamente
- El código debe compilar sin errores ni advertencias

## Licencia

Este proyecto está bajo la Licencia MIT. Consulte el archivo `LICENSE` para más detalles.

## Autores

- **David** - Desarrollador Principal - [@Davs07](https://github.com/Davs07)

## Soporte

Para reportar problemas o solicitar nuevas funcionalidades, por favor crear un issue en el repositorio de GitHub.

## Estado del Proyecto

Este proyecto está en desarrollo activo. Para la versión más reciente y actualizaciones, consultar la rama `master` del repositorio.
