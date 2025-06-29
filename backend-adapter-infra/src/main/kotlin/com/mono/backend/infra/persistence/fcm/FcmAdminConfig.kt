package com.mono.backend.infra.persistence.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource

@Primary
@Configuration
class FcmAdminConfig {
    @Bean
    fun firebaseApp(): FirebaseApp {
        val resource = ClassPathResource("frontend-58f8a-firebase-adminsdk-fbsvc-28257c7397.json")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
            .build()
        return FirebaseApp.initializeApp(options)
    }
}