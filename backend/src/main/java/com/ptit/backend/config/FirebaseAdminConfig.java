package com.ptit.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

@Slf4j
 @Configuration
@RequiredArgsConstructor
public class FirebaseAdminConfig {

    @Value("${firebase.project-id:}")
    private String firebaseProjectId;

    @Value("${firebase.service-account-path:}")
    private String firebaseServiceAccountPath;

    @PostConstruct
    public void init() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        try {
            FirebaseOptions.Builder builder = FirebaseOptions.builder();
            builder.setCredentials(resolveCredentials());

            if (StringUtils.hasText(firebaseProjectId)) {
                builder.setProjectId(firebaseProjectId.trim());
            }

            FirebaseApp.initializeApp(builder.build());
            log.info("Firebase Admin SDK initialized successfully");
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Khong the khoi tao Firebase Admin SDK. " +
                            "Hay thiet lap GOOGLE_APPLICATION_CREDENTIALS hoac FIREBASE_SERVICE_ACCOUNT_PATH " +
                            "tro den file service account JSON hop le.",
                    exception
            );
        }
    }

    private GoogleCredentials resolveCredentials() throws IOException {
        String googleApplicationCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (StringUtils.hasText(googleApplicationCredentials)) {
            try (InputStream inputStream = Files.newInputStream(Path.of(googleApplicationCredentials.trim()))) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        if (StringUtils.hasText(firebaseServiceAccountPath)) {
            try (InputStream inputStream = Files.newInputStream(Path.of(firebaseServiceAccountPath.trim()))) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        ClassPathResource classPathResource = new ClassPathResource("firebase-service-account.json");
        if (classPathResource.exists()) {
            try (InputStream inputStream = classPathResource.getInputStream()) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        throw new IllegalStateException("Khong tim thay Firebase service account JSON");
    }
}
