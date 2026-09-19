package com.depeche.infos.dto;


import com.depeche.infos.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilisateurRequestDTO {
    @NotBlank(message = "le nom est obligatoire")
    private String nom;
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    @NotNull(message = "Le type d'utilisateur est obligatoire")
    private RoleType typeUtilisateur;


}
