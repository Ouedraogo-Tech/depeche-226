package com.depeche.infos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Paramètres du footer : lus par tout le monde (GET), modifiés par le Webmaster (PUT).
@Getter
@Setter
public class ParametreSiteDTO {

    @NotBlank(message = "Le nom du site est obligatoire")
    @Size(max = 100, message = "Le nom du site ne doit pas dépasser 100 caractères")
    private String nomSite;

    @Size(max = 500, message = "Le slogan ne doit pas dépasser 500 caractères")
    private String slogan;

    @Email(message = "Le format de l'email de contact est invalide")
    private String emailContact;

    private String telephone;

    private String adresse;

    private String ville;

    private String mentionEdition;

    // Réseaux sociaux (facultatifs) : uniquement des liens https:// (jamais "javascript:" ou autre)
    @Size(max = 255, message = "Le lien Facebook est trop long")
    @Pattern(regexp = "^$|^https://.+", message = "Le lien Facebook doit commencer par https://")
    private String lienFacebook;

    @Size(max = 255, message = "Le lien YouTube est trop long")
    @Pattern(regexp = "^$|^https://.+", message = "Le lien YouTube doit commencer par https://")
    private String lienYoutube;

    @Size(max = 255, message = "Le lien WhatsApp est trop long")
    @Pattern(regexp = "^$|^https://.+", message = "Le lien WhatsApp doit commencer par https:// (ex. : https://wa.me/22670000000)")
    private String lienWhatsapp;

    @Size(max = 255, message = "Le lien Instagram est trop long")
    @Pattern(regexp = "^$|^https://.+", message = "Le lien Instagram doit commencer par https://")
    private String lienInstagram;

    @Size(max = 255, message = "Le lien LinkedIn est trop long")
    @Pattern(regexp = "^$|^https://.+", message = "Le lien LinkedIn doit commencer par https://")
    private String lienLinkedin;

    // Pages légales (texte libre, retours à la ligne conservés à l'affichage)
    @Size(max = 30000, message = "Les mentions légales sont trop longues (30 000 caractères maximum)")
    private String mentionsLegales;

    @Size(max = 30000, message = "La politique de confidentialité est trop longue (30 000 caractères maximum)")
    private String politiqueConfidentialite;

    // Rempli par le serveur, ignoré en entrée
    private LocalDateTime dateMiseAJour;
}
