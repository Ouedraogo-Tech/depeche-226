package com.depeche.infos.config;

import com.depeche.infos.service.MediaService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Sert les images téléversées : GET /uploads/xxxx.jpg -> fichier du dossier "uploads/"
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MediaService mediaService;

    public WebConfig(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String emplacement = mediaService.getDossierUpload().toUri().toString();
        // Spring exige un "/" final pour un dossier
        if (!emplacement.endsWith("/")) {
            emplacement += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(emplacement);
    }
}
