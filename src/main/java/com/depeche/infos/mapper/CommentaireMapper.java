package com.depeche.infos.mapper;

import com.depeche.infos.dto.CommentaireRequestDTO;
import com.depeche.infos.dto.CommentaireResponseDTO;
import com.depeche.infos.entity.Commentaire;
import org.springframework.stereotype.Component;


@Component
public class CommentaireMapper {

    public Commentaire toEntity(CommentaireRequestDTO request) {
        if (request == null) return null;
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(request.getContenu());
        return commentaire;
    }

    public CommentaireResponseDTO toResponseDTO(Commentaire commentaire) {
        if (commentaire == null) return null;
        CommentaireResponseDTO dto = new CommentaireResponseDTO();
        dto.setId(commentaire.getId());
        dto.setContenu(commentaire.getContenu());
        dto.setDateCreation(commentaire.getDateCreation());
        dto.setStatut(commentaire.getStatut());

        if (commentaire.getAuteur() != null) {
            dto.setAuteurId(commentaire.getAuteur().getId());
            dto.setAuteurNomComplet(commentaire.getAuteur().getNom() + " " + commentaire.getAuteur().getPrenom());
        }
        if (commentaire.getArticle() != null) {
            dto.setArticleId(commentaire.getArticle().getId());
            dto.setArticleTitre(commentaire.getArticle().getTitre());
        }
        return dto;
    }
}
