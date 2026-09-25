package com.depeche.infos.controller;

import com.depeche.infos.service.MediaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/medias")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // POST /api/medias (multipart/form-data, champ "fichier") -> { "url": "/uploads/xxxx.jpg" }
    // L'URL renvoyée se met ensuite dans le champ "image" de l'article.
    @PreAuthorize("hasAuthority('ARTICLE_ECRIRE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> televerserImage(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.status(201).body(Map.of("url", mediaService.enregistrerImage(fichier)));
    }
}
