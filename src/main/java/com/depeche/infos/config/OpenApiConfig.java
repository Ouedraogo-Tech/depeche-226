package com.depeche.infos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI depecheInfosOpenAPI() {
        return new OpenAPI()
                // 1. On dit à Swagger que toutes les routes exigent un token
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // 2. On définit comment fonctionne ce token (JWT)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // 3. Les infos de l'API
                .info(new Info()
                        .title("DéPêche Infos API")
                        .description("API REST pour la gestion des articles, catégories, commentaires et utilisateurs du média DéPêche Infos.")
                        .version("v1.0"));
    }
}