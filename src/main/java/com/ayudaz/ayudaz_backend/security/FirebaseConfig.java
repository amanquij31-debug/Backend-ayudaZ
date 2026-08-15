package com.ayudaz.ayudaz_backend.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Inicializa Firebase Admin SDK. Lee las credenciales desde la variable de
 * entorno FIREBASE_CONFIG_JSON (el JSON de la cuenta de servicio, codificado
 * en Base64). Esto evita subir el archivo firebase-service-account.json al
 * repositorio, que está (correctamente) excluido en .gitignore.
 *
 * Para desarrollo local, si FIREBASE_CONFIG_JSON no está definida, se intenta
 * leer el archivo firebase-service-account.json desde src/main/resources
 * (si existe localmente en tu máquina).
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.base64:}")
    private String firebaseConfigBase64;

    @PostConstruct
    public void init() throws Exception {

        InputStream serviceAccount;

        if (firebaseConfigBase64 != null && !firebaseConfigBase64.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(firebaseConfigBase64);
            serviceAccount = new ByteArrayInputStream(decoded);
        } else {
            // Fallback para desarrollo local: intenta leer el archivo del classpath
            serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");
        }

        if (serviceAccount == null) {
            throw new IllegalStateException(
                    "No se encontraron credenciales de Firebase. Define la variable de entorno " +
                    "FIREBASE_CONFIG_JSON con el contenido del archivo de credenciales codificado en Base64, " +
                    "o coloca firebase-service-account.json en src/main/resources para desarrollo local."
            );
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}