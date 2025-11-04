# 🚀 Deploy en Render - Spring Boot Backend

## Configuración de Render

### Opción 1: Usando Docker (Recomendado)

1. **En Render Dashboard:**
   - Name: `EventsManagement-PMI`
   - Environment: `Production`
   - Language: **Docker**
   - Branch: `Fer`
   - Region: `Oregon (US West)`
   - Root Directory: `Gestion_Eventos` (si aplica)

2. **Dockerfile está incluido** - Render lo detectará automáticamente

3. **Variables de Entorno requeridas:**
   ```
   DATABASE_URL=jdbc:mysql://host:port/database
   DB_USERNAME=tu_usuario
   DB_PASSWORD=tu_password
   MAIL_USERNAME=tu_correo@gmail.com
   MAIL_PASSWORD=tu_app_password
   SPRING_PROFILES_ACTIVE=production
   ```

---

### Opción 2: Sin Docker (Comandos Shell)

Si Render no permite Docker, usa:

**Language:** Shell

**Build Command:**
```bash
./mvnw clean install -DskipTests
```

**Start Command:**
```bash
java -Dserver.port=$PORT -jar target/*.jar
```

**Root Directory:** `Gestion_Eventos` (si el proyecto está en subdirectorio)

---

## Variables de Entorno

Agrega estas variables en Render Dashboard → Environment:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `production` |
| `AIVEN_DB_PASSWORD` | Password de Aiven | `(obtener de Aiven Console)` |
| `MAIL_USERNAME` | Email Gmail | `correo@gmail.com` |
| `MAIL_PASSWORD` | App Password | `xxxx xxxx xxxx xxxx` |
| `PORT` | Puerto (automático) | `8080` |

### Variables Opcionales (con valores por defecto):

| Variable | Valor por Defecto |
|----------|-------------------|
| `AIVEN_DATABASE_URL` | `jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED` |
| `AIVEN_DB_USERNAME` | `avnadmin` |

### Base de Datos Aiven

✅ **Base de datos:** MySQL en Aiven Cloud
- Host: `em-pmi-db-davs.k.aivencloud.com:16969`
- Database: `defaultdb`
- User: `avnadmin`
- SSL: Requerido

---

## 🔗 URLs después del deploy

- Backend API: `https://eventsmanagement-pmi.onrender.com/api`
- Health Check: `https://eventsmanagement-pmi.onrender.com/actuator/health` (si tienes Actuator)

Actualiza `NEXT_PUBLIC_API_URL` en el frontend con la URL del backend.

---

## 📊 Información de la Base de Datos Aiven

**Servicio:** MySQL en Aiven Cloud
```
Host: em-pmi-db-davs.k.aivencloud.com
Port: 16969
Database: defaultdb
User: avnadmin
Password: (configurar en variable AIVEN_DB_PASSWORD)
SSL Mode: REQUIRED
```

**Service URI Format:**
```
mysql://avnadmin:PASSWORD@em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED
```

**Características:**
- ✅ SSL/TLS automático
- ✅ Backups automáticos
- ✅ Alta disponibilidad
- ✅ Connection pooling configurado (max 5 conexiones)

**Acceso al Dashboard:**
- Panel Aiven: https://console.aiven.io/
- Monitoreo de métricas y logs disponibles

---

## Problemas Comunes

### Error: Java version incompatible
- Verifica que Render soporte Java 21+
- Considera bajar la versión en `pom.xml` si es necesario

### Error: mvnw permission denied
```bash
chmod +x mvnw
git add mvnw
git commit -m "Fix mvnw permissions"
git push
```

### Error: Out of memory
Ajusta la memoria en Start Command:
```bash
java -Xmx512m -Xms256m -Dserver.port=$PORT -jar target/*.jar
```

---

## 🔗 URLs después del deploy

- Backend API: `https://eventsmanagement-pmi.onrender.com/api`
- Health Check: `https://eventsmanagement-pmi.onrender.com/actuator/health`

Actualiza `NEXT_PUBLIC_API_URL` en el frontend con la URL del backend.
