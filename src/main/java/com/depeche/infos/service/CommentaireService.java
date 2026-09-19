package com.depeche.infos.service;


import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutCommentaire;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CommentaireRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CommentaireService(CommentaireRepository commentaireRepository, ArticleRepository articleRepository, UtilisateurRepository utilisateurRepository) {
        this.commentaireRepository = commentaireRepository;
        this.articleRepository = articleRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Commentaire ajouterCommentaire(Commentaire commentaire, Long auteurId, Long articleId) {
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new RessourceIntrouvableException("Auteur introuvable avec l'id : " + auteurId));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id : " + articleId));

        commentaire.setAuteur(auteur);
        commentaire.setArticle(article);
        commentaire.setStatut(StatutCommentaire.PUBLIE);
        commentaire.setDateCreation(LocalDateTime.now());

        return commentaireRepository.save(commentaire);
    }

    public List<Commentaire> listerCommentairesParArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id : " + articleId));
        return commentaireRepository.findByArticle(article);
    }

    public Commentaire masquerCommentaire(Long id) {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Commentaire introuvable avec l'id : " + id));
        commentaire.setStatut(StatutCommentaire.MASQUE);
        return commentaireRepository.save(commentaire);
    }


    public void supprimerCommentaire(Long id) {
        if (!commentaireRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Commentaire introuvable avec l'id : " + id);
        }
        commentaireRepository.deleteById(id);
    }

    public List<Commentaire> listerTousLesCommentaires() {
        return commentaireRepository.findAll();
    }
}
