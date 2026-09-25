package com.depeche.infos.dto;

import com.depeche.infos.enums.StatutArticle;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleResponseDTO {
    private Long id;
    private String titre;
    private String resume;
    private String contenu;
    private String image;
    private StatutArticle statut;
    private String commentaireEditorial;
    private LocalDateTime dateCreation;
    private LocalDateTime datePublication;
    private Long auteurId;
    private String auteurNomComplet;
    private Long categorieId;
    private String categorieNom;
}
