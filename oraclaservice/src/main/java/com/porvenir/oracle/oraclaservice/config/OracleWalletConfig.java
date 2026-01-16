package com.porvenir.oracle.oraclaservice.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.ByteArrayInputStream;

/**
 * Configuración que extrae el Oracle Wallet UNA SOLA VEZ al inicio del pod.
 * Después de esto, Spring Boot mantiene el connection pool activo.
 */
@Configuration
public class OracleWalletConfig {

    private static final Logger log = LoggerFactory.getLogger(OracleWalletConfig.class);

    @Value("${oracle.wallet.path}")
    private String walletPath;

    @Value("${ORACLE_WALLET:}")
    private String walletBase64;

    /**
     * Se ejecuta UNA SOLA VEZ cuando el bean se inicializa.
     * Extrae el wallet y lo deja listo para que el DataSource lo use.
     */
    @PostConstruct
    public void init() {
        log.info("=== Iniciando configuración de Oracle Wallet ===");

        if (walletBase64 == null || walletBase64.isEmpty()) {
            log.warn("ORACLE_WALLET variable no configurada. Omitiendo extracción.");
            return;
        }

        try {
            // Validar que el path del wallet exista o crearlo
            Path walletDir = Paths.get(walletPath);

            // Si el directorio ya existe con archivos, no extraer de nuevo
            if (Files.exists(walletDir) && Files.list(walletDir).findAny().isPresent()) {
                log.info("Wallet ya existe en: {}. Omitiendo extracción.", walletPath);
                return;
            }

            // Crear directorio si no existe
            Files.createDirectories(walletDir);
            log.info("Directorio de wallet creado: {}", walletPath);

            // Decodificar wallet desde Base64
            byte[] walletBytes = Base64.getDecoder().decode(walletBase64);
            log.info("Wallet decodificado. Tamaño: {} bytes", walletBytes.length);

            // Extraer archivos del ZIP
            int filesExtracted = extractZipFiles(walletBytes, walletDir);

            log.info("✓ Oracle Wallet extraído exitosamente");
            log.info("  - Ubicación: {}", walletPath);
            log.info("  - Archivos extraídos: {}", filesExtracted);
            log.info("  - TNS_ADMIN configurado en datasource URL");

        } catch (Exception e) {
            log.error("✗ Error al extraer Oracle Wallet", e);
            throw new RuntimeException("No se pudo inicializar el Oracle Wallet", e);
        }
    }

    /**
     * Extrae archivos del ZIP del wallet
     */
    private int extractZipFiles(byte[] zipBytes, Path targetDir) throws IOException {
        int count = 0;

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                // Omitir directorios
                if (entry.isDirectory()) {
                    continue;
                }

                // Crear archivo
                File file = new File(targetDir.toFile(), entry.getName());

                // Escribir contenido
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                log.debug("  Extraído: {}", entry.getName());
                count++;
                zis.closeEntry();
            }
        }

        return count;
    }
}