package com.depeche.infos.controller;

import com.depeche.infos.service.MediaService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

// GET /uploads/{nom} : PUBLIC (SecurityConfig) · les images envoyées sur le site, lues dans la base de données
@RestController
public class ImageController {

    private final MediaService mediaService;

    public ImageController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/uploads/{nom}")
    public ResponseEntity<byte[]> afficherImage(@PathVariable String nom) {
        return mediaService.trouver(nom)
                .map(media -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(media.getTypeContenu()))
                        // Un nom d'image ne change jamais de contenu : le navigateur peut la garder 1 an
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                        .body(media.getDonnees()))
                .orElse(ResponseEntity.notFound().build());
    }
}
