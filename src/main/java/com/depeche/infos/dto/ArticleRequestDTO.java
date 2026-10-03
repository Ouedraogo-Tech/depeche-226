package com.depeche.infos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequestDTO {

    // @NotBlank : Le titre ne peut pas être null ni vide (pas d'espaces uniquement)
    // @Size : la maquette limite le titre à 90 caractères
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 90, message = "Le titre ne doit pas dépasser 90 caractères")
    private String titre;

    // Chapeau affiché sur la page d'accueil (facultatif)
    @Size(max = 300, message = "Le résumé ne doit pas dépasser 300 caractères")
    private String resume;

    // @NotBlank : Le contenu est obligatoire
    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    // L'image n'est pas obligatoire : c'est l'URL renvoyée par POST /api/medias
    private String image;

    // Vidéo YouTube (facultatif) : seuls les liens youtube.com/watch, youtu.be et youtube.com/shorts sont acceptés
    // (on refuse tout autre site, pour éviter les liens piégés). Vide = pas de vidéo.
    @Size(max = 255, message = "Le lien de la vidéo est trop long")
    @Pattern(
            regexp = "^$|^https://(www\\.|m\\.)?(youtube\\.com/(watch\\?v=|shorts/)|youtu\\.be/)[A-Za-z0-9_-]{11}([?&#].*)?$",
            message = "Le lien de la vidéo doit être un lien YouTube (ex. : https://www.youtube.com/watch?v=… ou https://youtu.be/…)")
    private String lienVideo;

    // @NotNull : L'ID de la catégorie doit être fourni (il ne peut pas être null)
    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;
}
