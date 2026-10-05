package com.depeche.infos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// POST /api/auth/reinitialiser-mot-de-passe : { "jeton": "…reçu par e-mail…", "motDePasse": "nouveau" }
@Getter
@Setter
public class ReinitialisationMotDePasseDTO {

    @NotBlank(message = "Le lien de réinitialisation est incomplet")
    private String jeton;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
}
