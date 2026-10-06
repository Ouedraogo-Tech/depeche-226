package com.depeche.infos.dto;

import com.depeche.infos.enums.StatutArticle;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String lienVideo; // lien YouTube, ou null

    // Informations INTERNES à la rédaction : absentes des réponses publiques (voir ArticleMapper.toPublicDTO)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StatutArticle statut;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String commentaireEditorial;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime datePublicationSouhaitee;

    private LocalDateTime dateCreation;
    private LocalDateTime datePublication;
    private Long auteurId;
    private String auteurNomComplet;
    private Long categorieId;
    private String categorieNom;
}
