package com.depeche.infos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

// Chiffres affichés dans les cartes des dashboards (Admin, Responsable éditorial, Webmaster).
@Getter
@Setter
public class StatistiquesDTO {
    // ex : { "PUBLIE": 132, "BROUILLON": 9, "SOUMIS": 7, ... }
    private Map<String, Long> articlesParStatut;
    private long totalArticles;
    private long totalCategories;

    private long totalUtilisateurs;
    private long utilisateursActifs;
    private long utilisateursDesactives;
    private long totalRoles;

    private long totalCommentaires;
    private long commentairesMasques;

    private long abonnesActifs;
}
