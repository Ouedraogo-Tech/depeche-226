package com.depeche.infos.mapper;

import com.depeche.infos.dto.ArticleRequestDTO;
import com.depeche.infos.dto.ArticleResponseDTO;
import com.depeche.infos.entity.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    // Remarque : la catégorie n'est PAS remplie ici (le DTO ne contient que son id).
    // C'est ArticleService qui charge la Categorie depuis la base (création ET modification).
    public Article toEntity(ArticleRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Article article = new Article();
        article.setTitre(requestDTO.getTitre());
        article.setResume(requestDTO.getResume());
        article.setContenu(requestDTO.getContenu());
        article.setImage(requestDTO.getImage());
        // "" (champ vidé) → null : l'article n'a plus de vidéo
        String lien = requestDTO.getLienVideo();
        article.setLienVideo(lien == null || lien.isBlank() ? null : lien.trim());
        return article;
    }

    public ArticleResponseDTO toResponseDTO(Article article) {
        if (article == null) return null;
        ArticleResponseDTO dto = new ArticleResponseDTO();
        dto.setId(article.getId());
        dto.setTitre(article.getTitre());
        dto.setSlug(article.getSlug());
        dto.setResume(article.getResume());
        dto.setContenu(article.getContenu());
        dto.setLienVideo(article.getLienVideo());
        dto.setImage(article.getImage());
        dto.setStatut(article.getStatut());
        dto.setCommentaireEditorial(article.getCommentaireEditorial());
        dto.setDatePublicationSouhaitee(article.getDatePublicationSouhaitee());
        dto.setDateCreation(article.getDateCreation());
        dto.setDatePublication(article.getDatePublication());

        if (article.getAuteur() != null) {
            dto.setAuteurId(article.getAuteur().getId());
            // Correction : "Prénom Nom" (ex : "Aminata Sawadogo") comme dans les maquettes
            dto.setAuteurNomComplet(article.getAuteur().getPrenom() + " " + article.getAuteur().getNom());
        }
        if (article.getCategorie() != null) {
            dto.setCategorieId(article.getCategorie().getId());
            dto.setCategorieNom(article.getCategorie().getNom());
        }
        return dto;
    }

    // Version SITE PUBLIC : uniquement ce que le lecteur doit voir.
    // Le statut, le commentaire du responsable éditorial et la date souhaitée restent dans la rédaction
    // (ils n'apparaissent même pas dans les outils développeur F12).
    public ArticleResponseDTO toPublicDTO(Article article) {
        ArticleResponseDTO dto = toResponseDTO(article);
        if (dto == null) return null;
        dto.setStatut(null);
        dto.setCommentaireEditorial(null);
        dto.setDatePublicationSouhaitee(null);
        return dto;
    }
}
