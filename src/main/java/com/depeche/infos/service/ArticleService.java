package com.depeche.infos.service;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.enums.TypeNotification;
import com.depeche.infos.exception.ActionInterditeException;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import com.depeche.infos.repository.CommentaireRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    // Rôle qui peut agir sur les articles de TOUS les journalistes
    private static final String ROLE_RESPONSABLE = "RESPONSABLE_EDITORIAL";

    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;
    private final CommentaireRepository commentaireRepository;
    private final NotificationService notificationService;

    public ArticleService(ArticleRepository articleRepository, UtilisateurRepository utilisateurRepository,
                          CategorieRepository categorieRepository, CommentaireRepository commentaireRepository,
                          NotificationService notificationService) {
        this.articleRepository = articleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.categorieRepository = categorieRepository;
        this.commentaireRepository = commentaireRepository;
        this.notificationService = notificationService;
    }

    // ==================== LECTURE ====================

    public Optional<Article> rechercherArticleParId(Long id) {
        return articleRepository.findById(id);
    }

    // Tous les articles (back-office), les plus récents en premier
    public List<Article> listerTousLesArticles() {
        return articleRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCreation"));
    }

    // Filtre par statut (ex : SOUMIS pour la file de validation du responsable éditorial)
    public List<Article> listerArticlesParStatut(StatutArticle statut) {
        return articleRepository.findByStatutOrderByDateCreationDesc(statut);
    }

    // Site public : uniquement les articles PUBLIES, avec filtre facultatif par catégorie et mot-clé
    public List<Article> listerArticlesPublies(Long categorieId, String recherche) {
        String motCle = (recherche == null) ? null : recherche.trim().toLowerCase();
        return articleRepository.findByStatutOrderByDatePublicationDesc(StatutArticle.PUBLIE).stream()
                .filter(a -> categorieId == null || a.getCategorie().getId().equals(categorieId))
                .filter(a -> motCle == null || motCle.isEmpty()
                        || a.getTitre().toLowerCase().contains(motCle)
                        || (a.getResume() != null && a.getResume().toLowerCase().contains(motCle)))
                .toList();
    }

    // Site public : un article, seulement s'il est publié
    public Article trouverArticlePublie(Long id) {
        return articleRepository.findById(id)
                .filter(a -> a.getStatut() == StatutArticle.PUBLIE)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id " + id));
    }

    // 8. Lister les articles d'un auteur précis (pour "Mes articles" côté Journaliste)
    public List<Article> listerArticlesParAuteur(String emailAuteur) {
        return articleRepository.findByAuteurOrderByDateCreationDesc(trouverUtilisateur(emailAuteur));
    }

    // ==================== CRÉATION / MODIFICATION / SUPPRESSION ====================

    public Article creeArticle(Article article, Long auteurId, Long categorieId) {
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new RessourceIntrouvableException("Auteur introuvable avec l'id: " + auteurId));

        article.setAuteur(auteur);
        article.setStatut(StatutArticle.BROUILLON);
        article.setDateCreation(LocalDateTime.now());
        article.setCategorie(trouverCategorie(categorieId));
        return articleRepository.save(article);
    }

    // Correction : la catégorie était remise à null (le mapper ne la remplit pas),
    // ce qui faisait échouer toute modification. On la recharge maintenant depuis son id.
    public Article modifierArticle(Long id, Article articleModifie, Long categorieId, String emailConnecte) {
        Article articleExistant = trouverArticle(id);
        verifierDroits(articleExistant, emailConnecte);

        articleExistant.setTitre(articleModifie.getTitre());
        articleExistant.setResume(articleModifie.getResume());
        articleExistant.setContenu(articleModifie.getContenu());
        articleExistant.setImage(articleModifie.getImage());
        articleExistant.setCategorie(trouverCategorie(categorieId));
        return articleRepository.save(articleExistant);
    }

    // Correction : on supprime d'abord les commentaires de l'article,
    // sinon la clé étrangère commentaire.article_id bloque la suppression.
    @Transactional
    public void supprimerArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        verifierDroits(article, emailConnecte);
        commentaireRepository.deleteByArticle(article);
        articleRepository.delete(article);
    }

    // ==================== WORKFLOW ÉDITORIAL ====================

    public Article mettreEnBrouillons(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        verifierDroits(article, emailConnecte);
        article.setStatut(StatutArticle.BROUILLON);
        return articleRepository.save(article);
    }

    // Journaliste : BROUILLON ou A_REVISER -> SOUMIS, puis on prévient les responsables éditoriaux
    public Article soumettreArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        verifierDroits(article, emailConnecte);
        if (article.getStatut() != StatutArticle.BROUILLON && article.getStatut() != StatutArticle.A_REVISER) {
            throw new IllegalStateException("Seul un brouillon ou un article à réviser peut être soumis.");
        }
        article.setStatut(StatutArticle.SOUMIS);
        Article sauvegarde = articleRepository.save(article);

        notificationService.notifierRole(ROLE_RESPONSABLE, TypeNotification.ARTICLE_SOUMIS,
                "Nouvel article à valider : « " + article.getTitre() + " » par " + nomComplet(article.getAuteur()) + ".",
                article.getId());
        return sauvegarde;
    }

    // Publication directe (journaliste sur son article, ou responsable éditorial)
    public Article publierArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        Utilisateur connecte = verifierDroits(article, emailConnecte);
        article.setStatut(StatutArticle.PUBLIE);
        article.setDatePublication(LocalDateTime.now());
        Article sauvegarde = articleRepository.save(article);

        // Si ce n'est pas l'auteur qui publie, on le prévient
        if (!estAuteur(article, connecte)) {
            notificationService.notifier(article.getAuteur(), TypeNotification.ARTICLE_VALIDE,
                    "Votre article « " + article.getTitre() + " » a été publié.", article.getId());
        }
        return sauvegarde;
    }

    public Article planifierArticle(Long id, LocalDateTime datePublication, String emailConnecte) {
        Article article = trouverArticle(id);
        verifierDroits(article, emailConnecte);
        if (datePublication == null || !datePublication.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de publication doit être dans le futur.");
        }
        article.setStatut(StatutArticle.PLANIFIE);
        article.setDatePublication(datePublication);
        return articleRepository.save(article);
    }

    public Article archiverArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        verifierDroits(article, emailConnecte);
        article.setStatut(StatutArticle.ARCHIVE);
        return articleRepository.save(article);
    }

    // Responsable éditorial : SOUMIS -> PUBLIE (commentaire facultatif)
    public Article validerArticle(Long id, String commentaire) {
        Article article = trouverArticleSoumis(id);
        article.setStatut(StatutArticle.PUBLIE);
        article.setDatePublication(LocalDateTime.now());
        article.setCommentaireEditorial(nettoyer(commentaire));
        Article sauvegarde = articleRepository.save(article);

        notificationService.notifier(article.getAuteur(), TypeNotification.ARTICLE_VALIDE,
                "Votre article « " + article.getTitre() + " » a été validé et publié.", article.getId());
        return sauvegarde;
    }

    // Responsable éditorial : SOUMIS -> A_REVISER (commentaire obligatoire)
    public Article demanderModification(Long id, String commentaire) {
        String retour = commentaireObligatoire(commentaire);
        Article article = trouverArticleSoumis(id);
        article.setStatut(StatutArticle.A_REVISER);
        article.setCommentaireEditorial(retour);
        Article sauvegarde = articleRepository.save(article);

        notificationService.notifier(article.getAuteur(), TypeNotification.ARTICLE_A_REVISER,
                "Modification demandée sur « " + article.getTitre() + " » : " + retour, article.getId());
        return sauvegarde;
    }

    // Responsable éditorial : SOUMIS -> REFUSE (commentaire obligatoire)
    public Article refuserArticle(Long id, String commentaire) {
        String retour = commentaireObligatoire(commentaire);
        Article article = trouverArticleSoumis(id);
        article.setStatut(StatutArticle.REFUSE);
        article.setCommentaireEditorial(retour);
        Article sauvegarde = articleRepository.save(article);

        notificationService.notifier(article.getAuteur(), TypeNotification.ARTICLE_REFUSE,
                "Votre article « " + article.getTitre() + " » a été refusé : " + retour, article.getId());
        return sauvegarde;
    }

    // Tâche automatique (toutes les minutes) : publie les articles PLANIFIES dont la date est atteinte
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void publierArticlesPlanifies() {
        List<Article> aPublier = articleRepository
                .findByStatutAndDatePublicationLessThanEqual(StatutArticle.PLANIFIE, LocalDateTime.now());
        for (Article article : aPublier) {
            article.setStatut(StatutArticle.PUBLIE);
            articleRepository.save(article);
            notificationService.notifier(article.getAuteur(), TypeNotification.ARTICLE_PUBLIE,
                    "Votre article planifié « " + article.getTitre() + " » est maintenant en ligne.", article.getId());
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private Article trouverArticle(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id " + id));
    }

    private Article trouverArticleSoumis(Long id) {
        Article article = trouverArticle(id);
        if (article.getStatut() != StatutArticle.SOUMIS) {
            throw new IllegalStateException("Seul un article soumis peut être validé, renvoyé ou refusé.");
        }
        return article;
    }

    private Categorie trouverCategorie(Long categorieId) {
        if (categorieId == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        return categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RessourceIntrouvableException("Categorie introuvable avec l'id " + categorieId));
    }

    private Utilisateur trouverUtilisateur(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'email : " + email));
    }

    // Correction de sécurité : un journaliste ne peut agir QUE sur ses propres articles.
    // Le responsable éditorial peut agir sur tous les articles.
    private Utilisateur verifierDroits(Article article, String emailConnecte) {
        Utilisateur connecte = trouverUtilisateur(emailConnecte);
        boolean estResponsable = connecte.getRoles().stream()
                .anyMatch(role -> ROLE_RESPONSABLE.equals(role.getName()));
        if (!estAuteur(article, connecte) && !estResponsable) {
            throw new ActionInterditeException("Vous ne pouvez agir que sur vos propres articles.");
        }
        return connecte;
    }

    private boolean estAuteur(Article article, Utilisateur utilisateur) {
        return article.getAuteur() != null && article.getAuteur().getId().equals(utilisateur.getId());
    }

    private String commentaireObligatoire(String commentaire) {
        String retour = nettoyer(commentaire);
        if (retour == null) {
            throw new IllegalArgumentException("Un commentaire est obligatoire pour expliquer votre décision.");
        }
        return retour;
    }

    private String nettoyer(String texte) {
        return (texte == null || texte.isBlank()) ? null : texte.trim();
    }

    private String nomComplet(Utilisateur utilisateur) {
        return utilisateur == null ? "" : utilisateur.getPrenom() + " " + utilisateur.getNom();
    }
}
