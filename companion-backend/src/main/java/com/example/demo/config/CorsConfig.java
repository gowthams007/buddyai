package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Centralized CORS Configuration for BuddyAI
 * This configuration allows requests from Vercel frontend and local development.
 * Supports Vercel deployments (*.vercel.app) and Railway deployments (*.railway.app)
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    public CorsConfig() {
        logger.info("🔄 CorsConfig initialized - CORS is configured for Vercel and Railway");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("📡 Configuring CORS mappings for all origins matching *.vercel.app, *.railway.app, and localhost");
        
        registry.addMapping("/**")
                // Allow all Vercel and Railway subdomains plus localhost
                .allowedOriginPatterns("https://.*\\.vercel\\.app", "https://.*\\.railway\\.app", "http://localhost:*", "https://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin")
                .exposedHeaders("Content-Type", "Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
