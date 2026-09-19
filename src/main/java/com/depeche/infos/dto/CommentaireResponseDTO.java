package com.depeche.infos.dto;

import com.depeche.infos.enums.StatutCommentaire;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentaireResponseDTO {

    private Long id;
    private String contenu;
    private LocalDateTime dateCreation;
    private StatutCommentaire statut;

    private Long auteurId;
    private String auteurNomComplet;

    private Long articleId;
    private String articleTitre;
}
