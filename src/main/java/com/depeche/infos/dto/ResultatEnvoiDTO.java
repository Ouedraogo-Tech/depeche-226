package com.depeche.infos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// Réponse de POST /api/newsletter/envoyer : combien d'e-mails sont partis, et lesquels ont échoué
// Exemple : { "envoyes": 41, "echecs": 1, "adressesEnEchec": ["adresse@invalide"] }
@Getter
@AllArgsConstructor
public class ResultatEnvoiDTO {
    private int envoyes;
    private int echecs;
    private List<String> adressesEnEchec;
}
