package com.example.demo.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class NotificationService {

    @Value("${app.firebase.service-account-path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            // Provide a dummy initialization or use classpath resource
            // For MVP, if file doesn't exist, we skip to avoid crashing
            InputStream serviceAccount = getClass().getResourceAsStream("/firebase-service-account.json");
            if (serviceAccount != null && FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
        }
    }

    public void sendPushNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) return;

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}
