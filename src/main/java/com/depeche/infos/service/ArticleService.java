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

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    // RÈGLE : un BROUILLON est privé. Seul son auteur le voit, tant qu'il ne l'a pas soumis
    // (ni le responsable éditorial, ni l'administrateur, ni les autres journalistes).
    private boolean visiblePar(Article article, String emailConnecte) {
        return article.getStatut() != StatutArticle.BROUILLON
                || (article.getAuteur() != null && article.getAuteur().getEmail().equalsIgnoreCase(emailConnecte));
    }

    // Un article par son numéro (back-office) ; le brouillon d'un autre est « introuvable »
    public Optional<Article> rechercherArticleParId(Long id, String emailConnecte) {
        return articleRepository.findById(id).filter(a -> visiblePar(a, emailConnecte));
    }

    // Tous les articles (back-office), les plus récents en premier, sans les brouillons des autres
    public List<Article> listerTousLesArticles(String emailConnecte) {
        return articleRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCreation")).stream()
                .filter(a -> visiblePar(a, emailConnecte))
                .toList();
    }

    // Filtre par statut (ex : SOUMIS pour la file de validation du responsable éditorial)
    public List<Article> listerArticlesParStatut(StatutArticle statut, String emailConnecte) {
        return articleRepository.findByStatutOrderByDateCreationDesc(statut).stream()
                .filter(a -> visiblePar(a, emailConnecte))
                .toList();
    }

    // Site public : uniquement les articles PUBLIES, avec filtre facultatif par catégorie et mot-clé
    public List<Article> listerArticlesPublies(Long categorieId, String recherche) {
        // Recherche sans accents : "economie" trouve "Économie" (et inversement)
        String motCle = (recherche == null) ? null : normaliser(recherche.trim());
        return articleRepository.findByStatutOrderByDatePublicationDesc(StatutArticle.PUBLIE).stream()
                .filter(a -> categorieId == null || a.getCategorie().getId().equals(categorieId))
                .filter(a -> motCle == null || motCle.isEmpty()
                        || normaliser(a.getTitre()).contains(motCle)
                        || (a.getResume() != null && normaliser(a.getResume()).contains(motCle)))
                .toList();
    }

    // Site public : un article, seulement s'il est publié.
    // "lien" = l'adresse lisible (slug) ; un ancien lien avec le numéro (/articles/7) fonctionne encore.
    public Article trouverArticlePublie(String lien) {
        Optional<Article> article = lien.matches("\\d+")
                ? articleRepository.findById(Long.parseLong(lien))
                : articleRepository.findBySlug(lien);
        return article
                .filter(a -> a.getStatut() == StatutArticle.PUBLIE)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable."));
    }

    // Donne une adresse lisible aux articles créés avant l'ajout de cette fonctionnalité (appelé au démarrage)
    @Transactional
    public void attribuerSlugsManquants() {
        for (Article article : articleRepository.findBySlugIsNull()) {
            article.setSlug(genererSlug(article.getTitre()));
            articleRepository.save(article);
        }
    }

    // "AES, France, diaspora : Karamoko répond…" → "aes-france-diaspora-karamoko-repond"
    // (sans accents, 80 caractères max. coupés à la fin d'un mot ; "-2", "-3"… si le titre existe déjà)
    private String genererSlug(String titre) {
        String base = normaliser(titre == null ? "" : titre).replace("œ", "oe").replace("æ", "ae")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.length() > 80) {
            base = base.substring(0, 80);
            int dernierTiret = base.lastIndexOf('-');
            if (dernierTiret > 20) {
                base = base.substring(0, dernierTiret);
            }
        }
        if (base.isEmpty() || base.matches("\\d+")) {
            base = "article-" + base; // jamais un simple nombre (confondu avec un ancien lien /articles/7)
        }
        String slug = base;
        for (int n = 2; articleRepository.existsBySlug(slug); n++) {
            slug = base + "-" + n;
        }
        return slug;
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
        article.setSlug(genererSlug(article.getTitre()));
        return articleRepository.save(article);
    }

    // Correction : la catégorie était remise à null (le mapper ne la remplit pas),
    // ce qui faisait échouer toute modification. On la recharge maintenant depuis son id.
    public Article modifierArticle(Long id, Article articleModifie, Long categorieId, String emailConnecte) {
        Article articleExistant = trouverArticle(id);
        Utilisateur connecte = verifierDroits(articleExistant, emailConnecte);
        verifierStatutModifiable(articleExistant, connecte, "modifié");

        articleExistant.setTitre(articleModifie.getTitre());
        articleExistant.setResume(articleModifie.getResume());
        articleExistant.setContenu(articleModifie.getContenu());
        articleExistant.setImage(articleModifie.getImage());
        articleExistant.setLienVideo(articleModifie.getLienVideo());
        articleExistant.setCategorie(trouverCategorie(categorieId));
        return articleRepository.save(articleExistant);
    }

    // Correction : on supprime d'abord les commentaires de l'article,
    // sinon la clé étrangère commentaire.article_id bloque la suppression.
    @Transactional
    public void supprimerArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        Utilisateur connecte = verifierDroits(article, emailConnecte);
        verifierStatutModifiable(article, connecte, "supprimé");
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

    public Article soumettreArticle(Long id, String emailConnecte) {
        return soumettreArticle(id, null, emailConnecte);
    }

    // Journaliste : BROUILLON, A_REVISER ou REFUSE (après correction) -> SOUMIS, puis on prévient les responsables.
    // datePublicationSouhaitee (facultative) : appliquée seulement si l'article est validé.
    public Article soumettreArticle(Long id, LocalDateTime datePublicationSouhaitee, String emailConnecte) {
        Article article = trouverArticle(id);
        verifierDroits(article, emailConnecte);
        if (!STATUTS_SOUMETTABLES.contains(article.getStatut())) {
            throw new IllegalStateException("Seul un brouillon, un article à réviser ou un article refusé peut être soumis.");
        }
        if (datePublicationSouhaitee != null && !datePublicationSouhaitee.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de publication souhaitée doit être dans le futur.");
        }
        article.setStatut(StatutArticle.SOUMIS);
        article.setDatePublicationSouhaitee(datePublicationSouhaitee);
        Article sauvegarde = articleRepository.save(article);

        notificationService.notifierRole(ROLE_RESPONSABLE, TypeNotification.ARTICLE_SOUMIS,
                "Nouvel article à valider : « " + article.getTitre() + " » par " + nomComplet(article.getAuteur())
                        + (datePublicationSouhaitee != null ? " (publication souhaitée le " + FORMAT_DATE.format(datePublicationSouhaitee) + ")" : "")
                        + ".",
                article.getId());
        return sauvegarde;
    }

    private static final Set<StatutArticle> STATUTS_SOUMETTABLES =
            EnumSet.of(StatutArticle.BROUILLON, StatutArticle.A_REVISER, StatutArticle.REFUSE);
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH'h'mm");

    // Un article en relecture (SOUMIS) attend la décision du responsable éditorial :
    // personne ne peut le publier ni le planifier directement (sinon la validation serait contournée)
    private void refuserSiEnRelecture(Article article) {
        if (article.getStatut() == StatutArticle.SOUMIS) {
            throw new IllegalStateException("Cet article est en relecture : il sera publié après la décision du responsable éditorial.");
        }
    }

    // Publication directe (journaliste sur son article, ou responsable éditorial)
    public Article publierArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        Utilisateur connecte = verifierDroits(article, emailConnecte);
        refuserSiEnRelecture(article);
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
        refuserSiEnRelecture(article);
        if (datePublication == null || !datePublication.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de publication doit être dans le futur.");
        }
        article.setStatut(StatutArticle.PLANIFIE);
        article.setDatePublication(datePublication);
        return articleRepository.save(article);
    }

    // Archiver : l'article quitte le site public et l'historique par défaut, sans être supprimé.
    // L'auteur archive ses articles ; l'administrateur et le responsable éditorial, n'importe lequel (historique).
    public Article archiverArticle(Long id, String emailConnecte) {
        Article article = trouverArticle(id);
        Utilisateur connecte = trouverUtilisateur(emailConnecte);
        boolean gestionnaire = connecte.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || ROLE_RESPONSABLE.equals(role.getName()));
        if (!gestionnaire) {
            verifierDroits(article, emailConnecte);
        }
        refuserSiBrouillonDunAutre(article, connecte); // un brouillon reste privé, même pour l'admin
        refuserSiEnRelecture(article); // un article soumis attend d'abord la décision éditoriale
        if (article.getStatut() == StatutArticle.ARCHIVE) {
            throw new IllegalStateException("Cet article est déjà archivé.");
        }
        article.setStatut(StatutArticle.ARCHIVE);
        return articleRepository.save(article);
    }

    // Responsable éditorial : SOUMIS -> PUBLIE tout de suite, ou PLANIFIE si le journaliste a demandé
    // une date encore à venir (commentaire facultatif)
    public Article validerArticle(Long id, String commentaire) {
        Article article = trouverArticleSoumis(id);
        LocalDateTime souhaitee = article.getDatePublicationSouhaitee();
        boolean aPlanifier = souhaitee != null && souhaitee.isAfter(LocalDateTime.now());

        article.setStatut(aPlanifier ? StatutArticle.PLANIFIE : StatutArticle.PUBLIE);
        article.setDatePublication(aPlanifier ? souhaitee : LocalDateTime.now());
        article.setDatePublicationSouhaitee(null);
        article.setCommentaireEditorial(nettoyer(commentaire));
        Article sauvegarde = articleRepository.save(article);

        notificationService.notifier(article.getAuteur(), TypeNotification.ARTICLE_VALIDE,
                aPlanifier
                        ? "Votre article « " + article.getTitre() + " » a été validé : il sera publié le " + FORMAT_DATE.format(souhaitee) + "."
                        : "Votre article « " + article.getTitre() + " » a été validé et publié.",
                article.getId());
        return sauvegarde;
    }

    // Responsable éditorial : SOUMIS -> A_REVISER (commentaire obligatoire)
    public Article demanderModification(Long id, String commentaire) {
        String retour = commentaireObligatoire(commentaire);
        Article article = trouverArticleSoumis(id);
        article.setStatut(StatutArticle.A_REVISER);
        article.setDatePublicationSouhaitee(null); // la date demandée est annulée
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
        article.setDatePublicationSouhaitee(null); // la date demandée est annulée
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
        refuserSiBrouillonDunAutre(article, connecte);
        return connecte;
    }

    // Le brouillon d'un autre est privé : personne d'autre que son auteur ne peut agir dessus
    private void refuserSiBrouillonDunAutre(Article article, Utilisateur connecte) {
        if (article.getStatut() == StatutArticle.BROUILLON && !estAuteur(article, connecte)) {
            throw new RessourceIntrouvableException("Article introuvable.");
        }
    }

    // Statuts dans lesquels un journaliste peut modifier ou supprimer son article
    // (même règle que STATUTS_MODIFIABLES côté Angular). En relecture (SOUMIS), PLANIFIE ou ARCHIVE : refusé.
    private static final Set<StatutArticle> STATUTS_MODIFIABLES =
            EnumSet.of(StatutArticle.BROUILLON, StatutArticle.A_REVISER, StatutArticle.REFUSE, StatutArticle.PUBLIE);

    // Le responsable éditorial n'est pas limité ; le journaliste, si.
    private void verifierStatutModifiable(Article article, Utilisateur connecte, String action) {
        boolean estResponsable = connecte.getRoles().stream()
                .anyMatch(role -> ROLE_RESPONSABLE.equals(role.getName()));
        if (!estResponsable && !STATUTS_MODIFIABLES.contains(article.getStatut())) {
            throw new IllegalStateException("Cet article ne peut pas être " + action
                    + " : il est en relecture, planifié ou archivé.");
        }
    }

    // Recherche sans accents ni majuscules : "Économie" → "economie"
    private static String normaliser(String texte) {
        return Normalizer.normalize(texte, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
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
