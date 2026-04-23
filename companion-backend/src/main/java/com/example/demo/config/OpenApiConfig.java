package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuddyAI API Documentation")
                        .version("1.0")
                        .description("API for the BuddyAI Personal Assistant application. This API handles chat interactions, goal tracking, and reminder management.")
                        .contact(new Contact()
                                .name("BuddyAI Support")
                                .email("support@buddyai.com")));
    }
}
