package com.depeche.infos.config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI depecheInfosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dépèchê.Infos API")
                        .description("API REST pour la gestion du média DéPêche Infos.")
                        .version("v1.0"));
    }
}