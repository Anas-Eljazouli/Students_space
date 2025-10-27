package com.school.portal.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentPortalOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Student Portal API")
                        .description("API REST sécurisée pour la gestion du portail étudiant")
                        .version("v1")
                        .contact(new Contact().name("Digital Office").email("support@school.test")))
                .externalDocs(new ExternalDocumentation().description("Repository").url("https://example.com/student-portal"));
    }
}
