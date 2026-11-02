package com.depeche.infos.config;

import com.depeche.infos.entity.Administrateur;
import com.depeche.infos.entity.Permission;
import com.depeche.infos.entity.Role;
import com.depeche.infos.repository.PermissionRepository;
import com.depeche.infos.repository.RoleRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import com.depeche.infos.service.AbonneNewsletterService;
import com.depeche.infos.service.ArticleService;
import com.depeche.infos.service.ParametreSiteService;
import com.depeche.infos.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class DataInitializer {

    // Compte administrateur créé au premier démarrage (si aucun ADMIN n'existe).
    // AUCUNE valeur par défaut dans le code : l'e-mail et le mot de passe viennent UNIQUEMENT
    // de application.properties (fichier non envoyé sur GitHub).
    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, PermissionRepository permissionRepository,
                                   UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService,
                                   ParametreSiteService parametreSiteService, JdbcTemplate jdbcTemplate,
                                   AbonneNewsletterService abonneNewsletterService, ArticleService articleService) {
        return args -> {
            // 0. Correction : Hibernate a créé une contrainte CHECK sur article.statut avec l'ANCIENNE liste
            // de statuts. Elle bloquerait les nouveaux statuts (A_REVISER, REFUSE) : on la supprime.
            // (ddl-auto=update ne met jamais à jour une contrainte existante)
            jdbcTemplate.execute("ALTER TABLE IF EXISTS article DROP CONSTRAINT IF EXISTS article_statut_check");
            // Même problème pour les types de notification (nouveau type NOUVELLE_CATEGORIE)
            jdbcTemplate.execute("ALTER TABLE IF EXISTS notification DROP CONSTRAINT IF EXISTS notification_type_check");
            // Résumé agrandi de 300 à 1 000 caractères (ddl-auto=update n'agrandit jamais une colonne existante)
            jdbcTemplate.execute("ALTER TABLE IF EXISTS article ALTER COLUMN resume TYPE varchar(1000)");

            System.out.println(">>> Synchronisation des rôles et permissions...");

            // 1. CRÉATION DES PERMISSIONS
            Permission consulterActualites = createPermissionIfNotFound(permissionRepository, "ARTICLE_CONSULTER");
            Permission lireArticles = createPermissionIfNotFound(permissionRepository, "ARTICLE_LIRE");
            Permission mettreFavoris = createPermissionIfNotFound(permissionRepository, "ARTICLE_METTRE_FAVORIS");
            Permission consulterCategories = createPermissionIfNotFound(permissionRepository, "CATEGORIE_CONSULTER");
            Permission commenterArticles = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_ECRIRE");
            Permission abonnerNewsletter = createPermissionIfNotFound(permissionRepository, "NEWSLETTER_ABONNER");

            Permission redigerArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_ECRIRE");
            Permission modifierArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_MODIFIER");
            Permission enregistrerBrouillon = createPermissionIfNotFound(permissionRepository, "ARTICLE_BROUILLON_ENREGISTRER");
            Permission soumettreArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_SOUMETTRE");
            Permission publierArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_PUBLIER");
            Permission supprimerArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_SUPPRIMER");
            // Correction : utilisée par PATCH /articles/{id}/planifier mais jamais créée
            Permission planifierArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_PLANIFIER");
            // Nouveau : valider / demander une modification / refuser un article soumis
            Permission validerArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_VALIDER");
            // Nouveau : archiver un article (retiré du site et de l'historique par défaut, sans être supprimé)
            Permission archiverArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_ARCHIVER");


            Permission creerCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_CREER");
            Permission lireCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_LIRE");
            Permission modifierCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_MODIFIER");
            Permission supprimerCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_SUPPRIMER");


            Permission suivreStats = createPermissionIfNotFound(permissionRepository, "STATISTIQUES_SUIVRE");


            Permission masquerCommentaire = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_MASQUER");
            Permission publierCommentaire = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_PUBLIER");
            // Correction : utilisée par DELETE /commentaires/{id} mais jamais créée
            Permission supprimerCommentaire = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_SUPPRIMER");


            Permission gererNotifications = createPermissionIfNotFound(permissionRepository, "NOTIFICATION_GERER");
            // Nouveau : gérer la liste des abonnés (Webmaster)
            Permission gererNewsletter = createPermissionIfNotFound(permissionRepository, "NEWSLETTER_GERER");
            // Nouveau : gérer les paramètres du footer (Webmaster)
            Permission gererParametres = createPermissionIfNotFound(permissionRepository, "PARAMETRES_GERER");

            Permission creerComptes = createPermissionIfNotFound(permissionRepository, "UTILISATEUR_CREER");
            Permission attribuerRoles = createPermissionIfNotFound(permissionRepository, "ROLE_ATTRIBUER");
            Permission desactiverComptes = createPermissionIfNotFound(permissionRepository, "UTILISATEUR_DESACTIVER_SUSPENDRE");


            // 2. MISE À JOUR OU CRÉATION DES RÔLES (Find or Create)

            // RÔLE LECTEUR
            Role lecteur = roleRepository.findByName("LECTEUR").orElseGet(() -> new Role("LECTEUR"));
            lecteur.setPermissions(new HashSet<>(Arrays.asList(
                    consulterActualites, lireArticles, mettreFavoris, consulterCategories, commenterArticles, abonnerNewsletter
            )));
            roleRepository.save(lecteur);

            // RÔLE JOURNALISTE
            Role journaliste = roleRepository.findByName("JOURNALISTE").orElseGet(() -> new Role("JOURNALISTE"));
            journaliste.setPermissions(new HashSet<>(Arrays.asList(
                    redigerArticle, modifierArticle, publierArticle, soumettreArticle, consulterActualites, supprimerArticle,
                    enregistrerBrouillon, lireArticles, planifierArticle, archiverArticle
            )));
            roleRepository.save(journaliste);

            // RÔLE RESPONSABLE EDITORIAL
            Role responsable = roleRepository.findByName("RESPONSABLE_EDITORIAL").orElseGet(() -> new Role("RESPONSABLE_EDITORIAL"));
            responsable.setPermissions(new HashSet<>(Arrays.asList(
                    redigerArticle, modifierArticle, enregistrerBrouillon,
                    supprimerArticle, publierArticle, consulterActualites, creerCategorie, lireCategorie, modifierCategorie,
                    supprimerCategorie, suivreStats, planifierArticle, validerArticle, archiverArticle
            )));
            roleRepository.save(responsable);

            // RÔLE WEBMASTER
            Role webmaster = roleRepository.findByName("WEBMASTER").orElseGet(() -> new Role("WEBMASTER"));
            webmaster.setPermissions(new HashSet<>(Arrays.asList(
                    suivreStats, masquerCommentaire, publierCommentaire, supprimerCommentaire, gererNotifications,
                    gererNewsletter, gererParametres
            )));
            roleRepository.save(webmaster);

            // RÔLE ADMIN
            Role admin = roleRepository.findByName("ADMIN").orElseGet(() -> new Role("ADMIN"));
            admin.setPermissions(new HashSet<>(Arrays.asList(
                    consulterActualites, creerComptes, attribuerRoles, consulterCategories, desactiverComptes, suivreStats,
                    archiverArticle
            )));
            roleRepository.save(admin);

            System.out.println(">>> Synchronisation terminée avec succès !");

            // 3. Premier administrateur : POST /api/utilisateurs est réservé à l'admin,
            // il faut donc au moins un admin pour créer les autres comptes.
            if (utilisateurRepository.findByRoles_Name("ADMIN").isEmpty()
                    && (adminEmail.isBlank() || adminPassword.isBlank())) {
                System.out.println(">>> ATTENTION : aucun administrateur. Renseignez app.admin.email et "
                        + "app.admin.password dans application.properties, puis redémarrez.");
            } else if (utilisateurRepository.findByRoles_Name("ADMIN").isEmpty()) {
                Administrateur premierAdmin = new Administrateur();
                premierAdmin.setNom("Administrateur");
                premierAdmin.setPrenom("Principal");
                premierAdmin.setEmail(adminEmail);
                premierAdmin.setMotDePasse(adminPassword);
                utilisateurService.creerUtilisateur(premierAdmin);
                System.out.println(">>> Compte administrateur créé : " + adminEmail + " (changez le mot de passe !)");
            }

            // 4. Paramètres du footer par défaut
            parametreSiteService.obtenirParametres();
            // Page "À propos" : textes d'origine si le webmaster ne l'a jamais remplie
            parametreSiteService.initialiserAProposSiVide();

            // 5. Newsletter : les abonnés créés avant l'ajout du jeton en reçoivent un
            abonneNewsletterService.attribuerJetonsManquants();

            // 6. Adresse lisible (slug) pour les articles créés avant cette fonctionnalité
            articleService.attribuerSlugsManquants();
        };
    }

    private Permission createPermissionIfNotFound(PermissionRepository repository, String name) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Permission(name)));
    }
}
