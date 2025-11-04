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
| `DATABASE_URL` | URL de MySQL | `jdbc:mysql://host:3306/db` |
| `DB_USERNAME` | Usuario MySQL | `root` |
| `DB_PASSWORD` | Contraseña MySQL | `password` |
| `MAIL_USERNAME` | Email Gmail | `correo@gmail.com` |
| `MAIL_PASSWORD` | App Password | `xxxx xxxx xxxx xxxx` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `production` |
| `PORT` | Puerto (automático) | `8080` |

---

## Base de Datos en Render

### Crear MySQL Database:

1. En Render: **New** → **MySQL**
2. Name: `eventsmanagement-db`
3. User: `admin`
4. Copia las credenciales generadas
5. Úsalas en las variables de entorno del backend

---

## Health Check

Render verificará la salud del servicio en:
```
/actuator/health
```

Asegúrate de que Spring Boot Actuator esté configurado.

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
