package com.depeche.infos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Inscription publique d'un lecteur (POST /api/auth/inscription).
// Pas de champ "typeUtilisateur" : un visiteur ne peut créer QUE un compte LECTEUR.
@Getter
@Setter
public class InscriptionRequestDTO {

    @NotBlank(message = "le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    private String email;

    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
}
