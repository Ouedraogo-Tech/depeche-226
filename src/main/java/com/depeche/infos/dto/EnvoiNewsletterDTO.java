package com.depeche.infos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Ce que le webmaster envoie pour expédier la newsletter (POST /api/newsletter/envoyer)
// Exemple : { "objet": "La dépêche du 3 octobre", "abonneIds": [1, 4, 7], "articleIds": [15, 14, 12] }
@Getter
@Setter
public class EnvoiNewsletterDTO {

    @NotBlank(message = "L'objet de l'e-mail est obligatoire")
    @Size(max = 150, message = "L'objet ne doit pas dépasser 150 caractères")
    private String objet;

    @NotEmpty(message = "Choisissez au moins un abonné")
    private List<Long> abonneIds;

    @NotEmpty(message = "Choisissez au moins un article")
    @Size(max = 10, message = "10 articles maximum par newsletter")
    private List<Long> articleIds;
}
