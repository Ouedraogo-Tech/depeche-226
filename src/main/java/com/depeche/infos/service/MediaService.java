package com.depeche.infos.service;

import com.depeche.infos.entity.Media;
import com.depeche.infos.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

// Enregistre les images envoyées sur le site DANS la base de données (table "media")
// et renvoie l'URL publique à mettre dans le champ "image" de l'article : /uploads/{nom}
@Service
public class MediaService {

    // Types acceptés -> extension du nom enregistré
    private static final Map<String, String> TYPES_AUTORISES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    // Extension -> type, pour les images de l'ancien dossier "uploads/"
    private static final Map<String, String> TYPES_PAR_EXTENSION = Map.of(
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".png", "image/png",
            ".webp", "image/webp"
    );

    // Noms acceptés dans l'adresse /uploads/{nom} (jamais de "/" ni de "..")
    private static final Pattern NOM_VALIDE = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final MediaRepository mediaRepository;

    // Ancien dossier des images sur le disque : relu au démarrage pour importer en base les images manquantes
    private final Path ancienDossier;

    public MediaService(MediaRepository mediaRepository, @Value("${app.upload.dir:uploads}") String dossierUpload) {
        this.mediaRepository = mediaRepository;
        this.ancienDossier = Paths.get(dossierUpload).toAbsolutePath().normalize();
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
        String nom = UUID.randomUUID() + extension;
        try {
            mediaRepository.save(new Media(nom, fichier.getContentType(), fichier.getBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'enregistrer le fichier.");
        }
        return "/uploads/" + nom;
    }

    // L'image demandée à l'adresse /uploads/{nom} (vide si inconnue ou nom invalide)
    public Optional<Media> trouver(String nom) {
        if (nom == null || !NOM_VALIDE.matcher(nom).matches()) {
            return Optional.empty();
        }
        return mediaRepository.findByNom(nom);
    }

    // Au démarrage : les images encore dans l'ancien dossier "uploads/" sont copiées en base
    // (une seule fois chacune ; sur l'hébergeur, le dossier n'existe pas et rien ne se passe)
    public int importerAnciennesImages() {
        if (!Files.isDirectory(ancienDossier)) {
            return 0;
        }
        int importees = 0;
        try (DirectoryStream<Path> fichiers = Files.newDirectoryStream(ancienDossier)) {
            for (Path fichier : fichiers) {
                String nom = fichier.getFileName().toString();
                int point = nom.lastIndexOf('.');
                String type = point < 0 ? null : TYPES_PAR_EXTENSION.get(nom.substring(point).toLowerCase());
                if (type == null || !Files.isRegularFile(fichier) || !NOM_VALIDE.matcher(nom).matches()
                        || mediaRepository.existsByNom(nom)) {
                    continue;
                }
                mediaRepository.save(new Media(nom, type, Files.readAllBytes(fichier)));
                importees++;
            }
        } catch (IOException e) {
            System.out.println(">>> Import des anciennes images interrompu : " + e.getMessage());
        }
        return importees;
    }
}
