package com.depeche.infos.dto;


import com.depeche.infos.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Création d'un compte par l'Administrateur (POST /api/utilisateurs)
@Getter
@Setter
public class UtilisateurRequestDTO {

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

    @NotNull(message = "Le type d'utilisateur est obligatoire")
    private RoleType typeUtilisateur;
}
