package com.heim.api.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.admin.credentials}")
    private String firebaseAdminCredentials;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void initializeFirebase() {
        try {
            InputStream serviceAccount;

            // 1. Prioridad: ¿Es el JSON directo (Railway)?
            if (firebaseAdminCredentials != null && firebaseAdminCredentials.trim().startsWith("{")) {
                log.info("🚀 Cargando Firebase desde variable de entorno (JSON directo)");
                serviceAccount = new ByteArrayInputStream(firebaseAdminCredentials.getBytes(StandardCharsets.UTF_8));
            }
            // 2. Segundo caso: Es una ruta (Local / Default)
            else {
                log.info("📂 Cargando Firebase desde ruta: {}", firebaseAdminCredentials);
                Resource resource = resourceLoader.getResource(firebaseAdminCredentials);

                if (!resource.exists()) {
                    throw new IllegalStateException("❌ No se encontró el archivo de Firebase en: " + firebaseAdminCredentials);
                }
                serviceAccount = resource.getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Admin SDK inicializado correctamente.");
            }

            serviceAccount.close();

        } catch (Exception e) {
            log.error("❌ Error en Firebase: {}. La aplicación continuará pero sin notificaciones.", e.getMessage());
        }
    }
}
/*@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.admin.credentials}")
    private String firebaseAdminCredentials;

    @Autowired
    private ResourceLoader resourceLoader;

   @PostConstruct
    public void initializeFirebase() {
        try {
            log.info("🚀 Intentando cargar: {}", firebaseAdminCredentials);

            Resource resource = resourceLoader.getResource(firebaseAdminCredentials);

            if (!resource.exists()) {
                throw new IllegalStateException("❌ El archivo no existe en la ruta definida");
            }

         try(InputStream serviceAccount = resource.getInputStream();){
             FirebaseOptions options = FirebaseOptions.builder()
                     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                     .build();

             if (FirebaseApp.getApps().isEmpty()) {
                 FirebaseApp.initializeApp(options);
                 log.info("✅ Firebase Admin SDK inicializado correctamente.");
             }
         }

        } catch (Exception e) {
            log.error("❌ Error crítico: {}", e.getMessage());
        }
    }

}*/
