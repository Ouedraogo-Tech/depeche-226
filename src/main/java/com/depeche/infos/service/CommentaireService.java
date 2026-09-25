package com.depeche.infos.service;


import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.enums.StatutCommentaire;
import com.depeche.infos.enums.TypeNotification;
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
    private final NotificationService notificationService;

    public CommentaireService(CommentaireRepository commentaireRepository, ArticleRepository articleRepository,
                              UtilisateurRepository utilisateurRepository, NotificationService notificationService) {
        this.commentaireRepository = commentaireRepository;
        this.articleRepository = articleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService;
    }

    public Commentaire ajouterCommentaire(Commentaire commentaire, Long auteurId, Long articleId) {
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new RessourceIntrouvableException("Auteur introuvable avec l'id : " + auteurId));
        Article article = trouverArticle(articleId);

        // Correction : on ne peut commenter qu'un article publié
        if (article.getStatut() != StatutArticle.PUBLIE) {
            throw new IllegalStateException("On ne peut commenter qu'un article publié.");
        }

        commentaire.setAuteur(auteur);
        commentaire.setArticle(article);
        commentaire.setStatut(StatutCommentaire.PUBLIE);
        commentaire.setDateCreation(LocalDateTime.now());
        Commentaire sauvegarde = commentaireRepository.save(commentaire);

        // On prévient le journaliste auteur de l'article
        notificationService.notifier(article.getAuteur(), TypeNotification.NOUVEAU_COMMENTAIRE,
                auteur.getPrenom() + " " + auteur.getNom() + " a commenté « " + article.getTitre() + " ».",
                article.getId());
        return sauvegarde;
    }

    // Site public : uniquement les commentaires PUBLIES d'un article
    // (Correction : avant, les commentaires masqués étaient aussi renvoyés)
    public List<Commentaire> listerCommentairesParArticle(Long articleId) {
        return commentaireRepository.findByArticleAndStatutOrderByDateCreationDesc(
                trouverArticle(articleId), StatutCommentaire.PUBLIE);
    }

    // Webmaster : tous les commentaires, ou filtrés par statut
    public List<Commentaire> listerTousLesCommentaires(StatutCommentaire statut) {
        return (statut == null)
                ? commentaireRepository.findAllByOrderByDateCreationDesc()
                : commentaireRepository.findByStatutOrderByDateCreationDesc(statut);
    }

    public List<Commentaire> listerTousLesCommentaires() {
        return listerTousLesCommentaires(null);
    }

    // Journaliste : commentaires reçus sur ses articles
    public List<Commentaire> listerCommentairesSurMesArticles(String email) {
        Utilisateur auteur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'email : " + email));
        return commentaireRepository.findByArticle_AuteurOrderByDateCreationDesc(auteur);
    }

    public Commentaire masquerCommentaire(Long id) {
        Commentaire commentaire = trouverCommentaire(id);
        commentaire.setStatut(StatutCommentaire.MASQUE);
        return commentaireRepository.save(commentaire);
    }

    // Nouveau : rendre visible un commentaire masqué (bouton "Publier" de la maquette Webmaster)
    public Commentaire publierCommentaire(Long id) {
        Commentaire commentaire = trouverCommentaire(id);
        commentaire.setStatut(StatutCommentaire.PUBLIE);
        return commentaireRepository.save(commentaire);
    }

    public void supprimerCommentaire(Long id) {
        if (!commentaireRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Commentaire introuvable avec l'id : " + id);
        }
        commentaireRepository.deleteById(id);
    }

    private Commentaire trouverCommentaire(Long id) {
        return commentaireRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Commentaire introuvable avec l'id : " + id));
    }

    private Article trouverArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id : " + articleId));
    }
}
