package com.api.gestion.eventos.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Clase de configuración para verificar y loggear la configuración de la base de datos
 * en el entorno de producción. Útil para debugging de problemas de conexión.
 */
@Configuration
@Profile("production")
@Slf4j
public class DatabaseConnectionChecker {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @PostConstruct
    public void logDatabaseConfig() {
        log.info("==========================================================");
        log.info("DATABASE CONNECTION CONFIGURATION CHECK");
        log.info("==========================================================");
        log.info("Database URL: {}", databaseUrl);
        log.info("Database Username: {}", username);
        log.info("Database Password: {}", (password != null && !password.isEmpty()) ? "***SET*** (length: " + password.length() + ")" : "***NOT SET***");
        log.info("Active Profile: production");
        log.info("==========================================================");
        
        // Verificar variables de entorno del sistema
        log.info("ENVIRONMENT VARIABLES CHECK:");
        log.info("AIVEN_DATABASE_URL exists: {}", System.getenv("AIVEN_DATABASE_URL") != null);
        log.info("AIVEN_DB_USERNAME exists: {}", System.getenv("AIVEN_DB_USERNAME") != null);
        log.info("AIVEN_DB_PASSWORD exists: {}", System.getenv("AIVEN_DB_PASSWORD") != null);
        log.info("MAIL_USERNAME exists: {}", System.getenv("MAIL_USERNAME") != null);
        log.info("MAIL_PASSWORD exists: {}", System.getenv("MAIL_PASSWORD") != null);
        log.info("PORT: {}", System.getenv("PORT"));
        log.info("SPRING_PROFILES_ACTIVE: {}", System.getenv("SPRING_PROFILES_ACTIVE"));
        log.info("==========================================================");
        
        // Validar que la URL contiene el host correcto
        if (databaseUrl.contains("localhost") || databaseUrl.contains("127.0.0.1")) {
            log.error("⚠️  WARNING: Database URL points to localhost! This will fail in production.");
            log.error("⚠️  Expected URL should contain: em-pmi-db-davs.k.aivencloud.com");
        } else if (databaseUrl.contains("em-pmi-db-davs.k.aivencloud.com")) {
            log.info("✅ Database URL correctly points to Aiven MySQL");
        }
        
        // Validar SSL
        if (databaseUrl.contains("ssl-mode=REQUIRED")) {
            log.info("✅ SSL mode is enabled (required for Aiven)");
        } else {
            log.warn("⚠️  SSL mode not detected in URL. Aiven requires SSL!");
        }
        
        log.info("==========================================================");
    }
}
