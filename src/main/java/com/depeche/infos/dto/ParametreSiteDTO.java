package com.depeche.infos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    // Rempli par le serveur, ignoré en entrée
    private LocalDateTime dateMiseAJour;
}
