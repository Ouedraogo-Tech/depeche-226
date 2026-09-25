package com.depeche.infos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

// Enregistre les images des articles sur le disque (dossier "uploads/" par défaut)
// et renvoie l'URL publique à mettre dans le champ "image" de l'article.
@Service
public class MediaService {

    // Types acceptés -> extension du fichier enregistré
    private static final Map<String, String> TYPES_AUTORISES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final Path dossierUpload;

    public MediaService(@Value("${app.upload.dir:uploads}") String dossierUpload) {
        this.dossierUpload = Paths.get(dossierUpload).toAbsolutePath().normalize();
    }

    public String enregistrerImage(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier reçu.");
        }
        String extension = TYPES_AUTORISES.get(fichier.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("Format non supporté : seules les images JPG, PNG ou WEBP sont acceptées.");
        }

        // Nom aléatoire : évite les collisions et les noms de fichiers dangereux
        String nomFichier = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(dossierUpload);
            Files.copy(fichier.getInputStream(), dossierUpload.resolve(nomFichier), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'enregistrer le fichier.");
        }
        return "/uploads/" + nomFichier;
    }

    public Path getDossierUpload() {
        return dossierUpload;
    }
}
