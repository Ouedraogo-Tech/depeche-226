package com.depeche.infos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// POST /api/auth/mot-de-passe-oublie : { "email": "lecteur@exemple.com" }
@Getter
@Setter
public class MotDePasseOublieDTO {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    private String email;
}
