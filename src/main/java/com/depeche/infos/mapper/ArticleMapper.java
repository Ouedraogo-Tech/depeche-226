package com.depeche.infos.mapper;

import com.depeche.infos.dto.ArticleRequestDTO;
import com.depeche.infos.dto.ArticleResponseDTO;
import com.depeche.infos.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public Article toEntity(ArticleRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Article article = new Article();
        article.setTitre(requestDTO.getTitre());
        article.setContenu(requestDTO.getContenu());
        article.setImage(requestDTO.getImage());
        return article;
    }

    public ArticleResponseDTO toResponseDTO(Article article) {
        if (article == null) return null;
        ArticleResponseDTO dto = new ArticleResponseDTO();
        dto.setId(article.getId());
        dto.setTitre(article.getTitre());
        dto.setContenu(article.getContenu());
        dto.setImage(article.getImage());
        dto.setStatut(article.getStatut());
        dto.setDateCreation(article.getDateCreation());
        dto.setDatePublication(article.getDatePublication());

        if (article.getAuteur() != null) {
            dto.setAuteurId(article.getAuteur().getId());
            dto.setAuteurNomComplet(article.getAuteur().getNom() + " " + article.getAuteur().getPrenom());
        }
        if (article.getCategorie() != null) {
            dto.setCategorieId(article.getCategorie().getId());
            dto.setCategorieNom(article.getCategorie().getNom());
        }
        return dto;
    }
}