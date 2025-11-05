# 🚀 Deploy en Render - Spring Boot Backend

## ⚠️ SOLUCIÓN AL ERROR "Communications link failure - Connection refused"

Si ves este error en los logs de Render, significa que **las variables de entorno NO están configuradas correctamente**.

### ✅ **SOLUCIÓN RÁPIDA:**

1. **Ve a Render Dashboard** → tu servicio → **Environment** (menú izquierdo)
2. **BORRA todas las variables existentes** (si las hay)
3. **Copia y pega EXACTAMENTE estas variables** (una por una):

```plaintext
SPRING_PROFILES_ACTIVE=production
AIVEN_DATABASE_URL=jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED
AIVEN_DB_USERNAME=avnadmin
AIVEN_DB_PASSWORD=<obtener_de_Aiven_Console>
MAIL_USERNAME=tu_correo@gmail.com
MAIL_PASSWORD=xxxx_xxxx_xxxx_xxxx
```

4. **Haz clic en "Save Changes"**
5. **Render hará re-deploy automáticamente**

### 🔍 **Verificar en los Logs:**

Después del deploy, abre los logs y busca esto:

```
==========================================================
DATABASE CONNECTION CONFIGURATION CHECK
==========================================================
Database URL: jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED
Database Username: avnadmin
Database Password: ***SET*** (length: 24)
Active Profile: production
==========================================================
ENVIRONMENT VARIABLES CHECK:
AIVEN_DATABASE_URL exists: true ✅
AIVEN_DB_USERNAME exists: true ✅
AIVEN_DB_PASSWORD exists: true ✅
✅ Database URL correctly points to Aiven MySQL
✅ SSL mode is enabled (required for Aiven)
==========================================================
```

**Si ves `exists: false`** en alguna variable → **NO está configurada en Render**.

**Si ves `localhost` en la URL** → **Render NO está leyendo las variables**.

---

## Configuración de Render

### Opción 1: Usando Docker (Recomendado)

1. **En Render Dashboard - Create New Service:**
   - **Name:** `EventsManagement-PMI`
   - **Environment:** `Docker`
   - **Repository:** `https://github.com/Davs07/EventsManagement-PMI`
   - **Branch:** `Fer`
   - **Root Directory:** `Gestion_Eventos`
   - **Region:** `Oregon (US West)` (o el más cercano)

2. **Dockerfile Detection:**
   - Render detectará automáticamente el `Dockerfile`
   - NO necesitas configurar Build Command ni Start Command

3. **⚠️ CRÍTICO - Variables de Entorno:**
   
   En la sección **Environment**, agrega EXACTAMENTE estas variables:

   | Key | Value |
   |-----|-------|
   | `SPRING_PROFILES_ACTIVE` | `production` |
   | `AIVEN_DATABASE_URL` | `jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED` |
   | `AIVEN_DB_USERNAME` | `avnadmin` |
   | `AIVEN_DB_PASSWORD` | `<obtener_de_Aiven_Console>` |
   | `MAIL_USERNAME` | `tu_correo@gmail.com` |
   | `MAIL_PASSWORD` | `tu_app_password_de_16_caracteres` |

   **⚠️ NOTAS IMPORTANTES:**
   - Los nombres de las variables son **case-sensitive** (mayúsculas/minúsculas importan)
   - NO uses `DATABASE_URL`, debe ser `AIVEN_DATABASE_URL`
   - NO uses `DB_USERNAME`, debe ser `AIVEN_DB_USERNAME`
   - NO agregues comillas (`"`) alrededor de los valores
   - Render asigna automáticamente la variable `PORT`, NO la agregues manualmente

4. **Deploy:**
   - Haz clic en **"Create Web Service"**
   - Render iniciará el build y deploy automáticamente

---

### Opción 2: Sin Docker (Comandos Shell)

Si Render no permite Docker, usa:

**Language:** Shell

**Build Command:**
```bash
# Render soporta máximo Java 21
./mvnw clean package -Pproduction -DskipTests
```

**Start Command:**
```bash
java -Dserver.port=$PORT -Dspring.profiles.active=production -jar target/*.jar
```

**Root Directory:** `Gestion_Eventos` (si el proyecto está en subdirectorio)

> **IMPORTANTE:** El proyecto está configurado para compilar con Java 21 en producción (perfil `production`). Render y la mayoría de PaaS soportan hasta Java 21. Si usas Docker, el Dockerfile ya usa Java 21.

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

**Credenciales de conexión:**
```
Host: em-pmi-db-davs.k.aivencloud.com
Port: 16969
Database: defaultdb
User: avnadmin
Password: <obtener_de_Aiven_Console>
SSL Mode: REQUIRED
```

**JDBC URL completa:**
```
jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED
```

**Características:**
- ✅ SSL/TLS automático (requerido)
- ✅ Backups automáticos
- ✅ Alta disponibilidad
- ✅ Connection pooling (configurado para 3 conexiones máx en free tier)

**Acceso al Dashboard:**
- 🔗 Panel Aiven: https://console.aiven.io/

---

## 🐛 Debugging - Problemas Comunes

### ❌ Error: "Communications link failure - Connection refused"

**Síntoma en los logs:**
```
ERROR o.h.engine.jdbc.spi.SqlExceptionHelper : Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
Caused by: java.net.ConnectException: Connection refused
```

**Causa:** Las variables de entorno NO están configuradas o tienen nombres incorrectos.

**Solución paso a paso:**

1. **Verifica las variables en Render:**
   - Ve a tu servicio → **Environment**
   - Asegúrate de que existan EXACTAMENTE estas variables:
     - `SPRING_PROFILES_ACTIVE`
     - `AIVEN_DATABASE_URL`
     - `AIVEN_DB_USERNAME`
     - `AIVEN_DB_PASSWORD`
     - `MAIL_USERNAME`
     - `MAIL_PASSWORD`

2. **Verifica los NOMBRES de las variables:**
   - ❌ INCORRECTO: `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD`
   - ✅ CORRECTO: `AIVEN_DATABASE_URL`, `AIVEN_DB_USERNAME`, `AIVEN_DB_PASSWORD`

3. **Verifica los logs después del deploy:**
   - Busca la sección `DATABASE CONNECTION CONFIGURATION CHECK`
   - Debe decir: `AIVEN_DATABASE_URL exists: true`
   - Si dice `false`, la variable NO está configurada

4. **Re-deploya manualmente:**
   - Render Dashboard → tu servicio
   - Haz clic en **"Manual Deploy"** → **"Deploy latest commit"**

---

### ❌ Error: "Access denied for user 'avnadmin'@'...' "

**Causa:** Password incorrecto.

**Solución:**
1. Ve a Aiven Console → tu servicio MySQL
2. Copia el password exacto (incluyendo mayúsculas/minúsculas)
3. Actualiza `AIVEN_DB_PASSWORD` en Render
4. Guarda y espera el re-deploy automático

---

### ❌ Error: "Unknown database 'defaultdb'"

**Causa:** El nombre de la base de datos es incorrecto.

**Solución:**
1. Ve a Aiven Console
2. Verifica el nombre exacto de tu base de datos (puede ser diferente a `defaultdb`)
3. Actualiza la URL en `AIVEN_DATABASE_URL`:
   ```
   jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/TU_NOMBRE_DB?ssl-mode=REQUIRED
   ```

---

### ❌ Logs muestran "Database URL: jdbc:mysql://localhost:3306/..."

**Causa:** Render NO está leyendo las variables de entorno.

**Solución:**
1. Verifica que el nombre de la variable sea `AIVEN_DATABASE_URL` (NO `DATABASE_URL`)
2. Verifica que `SPRING_PROFILES_ACTIVE=production` esté configurado
3. Re-deploya manualmente

---

### ✅ Logs correctos - Todo funcionando:

Deberías ver esto en los logs:

```
==========================================================
DATABASE CONNECTION CONFIGURATION CHECK
==========================================================
Database URL: jdbc:mysql://em-pmi-db-davs.k.aivencloud.com:16969/defaultdb?ssl-mode=REQUIRED
Database Username: avnadmin
Database Password: ***SET*** (length: 24)
Active Profile: production
==========================================================
ENVIRONMENT VARIABLES CHECK:
AIVEN_DATABASE_URL exists: true
AIVEN_DB_USERNAME exists: true
AIVEN_DB_PASSWORD exists: true
✅ Database URL correctly points to Aiven MySQL
✅ SSL mode is enabled (required for Aiven)
==========================================================
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Hibernate: create table if not exists asistencia ...
Started Application in 8.543 seconds
```

---
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

## 📦 Build Local para Producción

### Windows (cmd / PowerShell)

**Usando Maven Wrapper (recomendado):**
```bash
# PowerShell
& '.\Gestion_Eventos\mvnw.cmd' -f '.\Gestion_Eventos\pom.xml' -Pproduction -DskipTests package

# CMD
.\Gestion_Eventos\mvnw.cmd -f .\Gestion_Eventos\pom.xml -Pproduction -DskipTests package
```

**Usando Maven instalado:**
```bash
mvn -f Gestion_Eventos\pom.xml -Pproduction -DskipTests package
```

El JAR compilado estará en: `Gestion_Eventos/target/api.gestion.eventos-0.0.1-SNAPSHOT.jar`

### Linux / macOS

```bash
./Gestion_Eventos/mvnw -f Gestion_Eventos/pom.xml -Pproduction -DskipTests package
```

### Configuración de Java

El proyecto utiliza **perfiles de Maven** para manejar diferentes versiones de Java:

- **Perfil `production`** (activo por defecto): Compila con **Java 21**
  - Compatible con Render, Railway, y la mayoría de plataformas cloud
  - El `Dockerfile` usa Eclipse Temurin JRE 21
  - El `maven-enforcer-plugin` valida que el build use JDK 21+

- **Perfil `dev`** (desarrollo local): Compila con **Java 25**
  - Actívalo con: `mvnw.cmd -Pdev clean install`
  - Solo para desarrollo local si tienes JDK 25 instalado

**Nota:** El perfil `production` garantiza compatibilidad con plataformas cloud que solo soportan Java 21.

---

## Problemas Comunes

### Error: Java version incompatible
- ✅ **Solución:** El proyecto ya está configurado para compilar con Java 21 en producción
- El perfil `production` está activo por defecto
- Render/Railway usan el `Dockerfile` que tiene JRE 21 (Eclipse Temurin)
- Si compilas localmente con JDK 25, el artefacto será compatible con JRE 21 gracias a `<release>21</release>`

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
