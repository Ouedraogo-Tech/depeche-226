package com.depeche.infos.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Corps des actions du responsable éditorial : valider / demander une modification / refuser.
// Le commentaire est obligatoire pour "demander une modification" et "refuser" (vérifié dans le service).
@Getter
@Setter
public class DecisionEditorialeDTO {

    @Size(max = 2000, message = "Le commentaire ne doit pas dépasser 2000 caractères")
    private String commentaire;
}
