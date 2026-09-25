package com.depeche.infos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Modification d'un compte : par l'Admin (PUT /api/utilisateurs/{id})
// ou par l'utilisateur lui-même (PUT /api/utilisateurs/moi).
// L'email et le rôle ne se modifient pas ici.
@Getter
@Setter
public class UtilisateurModificationDTO {

    @NotBlank(message = "le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    private String telephone;

    // Facultatif : vide = on garde l'ancien mot de passe
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
}
