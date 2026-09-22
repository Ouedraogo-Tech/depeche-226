package com.depeche.infos.config;

import com.depeche.infos.entity.Permission;
import com.depeche.infos.entity.Role;
import com.depeche.infos.repository.PermissionRepository;
import com.depeche.infos.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
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


            Permission creerCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_CREER");
            Permission lireCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_LIRE");
            Permission modifierCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_MODIFIER");
            Permission supprimerCategorie = createPermissionIfNotFound(permissionRepository, "CATEGORIE_SUPPRIMER");



            Permission suivreStats = createPermissionIfNotFound(permissionRepository, "STATISTIQUES_SUIVRE");


            Permission masquerCommentaire = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_MASQUER");
            Permission publierCommentaire = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_PUBLIER");



            Permission gererNotifications = createPermissionIfNotFound(permissionRepository, "NOTIFICATION_GERER");

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
                    redigerArticle, modifierArticle, publierArticle, soumettreArticle, consulterActualites, supprimerArticle, enregistrerBrouillon, lireArticles
            )));
            roleRepository.save(journaliste);

            // RÔLE RESPONSABLE EDITORIAL
            Role responsable = roleRepository.findByName("RESPONSABLE_EDITORIAL").orElseGet(() -> new Role("RESPONSABLE_EDITORIAL"));
            responsable.setPermissions(new HashSet<>(Arrays.asList(
                    redigerArticle, modifierArticle, enregistrerBrouillon,
                    supprimerArticle, publierArticle, consulterActualites, creerCategorie, lireCategorie, modifierCategorie, supprimerCategorie, suivreStats
            )));
            roleRepository.save(responsable);

            // RÔLE WEBMASTER
            Role webmaster = roleRepository.findByName("WEBMASTER").orElseGet(() -> new Role("WEBMASTER"));
            webmaster.setPermissions(new HashSet<>(Arrays.asList(
                    suivreStats, masquerCommentaire, publierCommentaire, gererNotifications
            )));
            roleRepository.save(webmaster);

            // RÔLE ADMIN
            Role admin = roleRepository.findByName("ADMIN").orElseGet(() -> new Role("ADMIN"));
            admin.setPermissions(new HashSet<>(Arrays.asList(
                    consulterActualites, creerComptes, attribuerRoles, consulterCategories, desactiverComptes
            )));
            roleRepository.save(admin);

            System.out.println(">>> Synchronisation terminée avec succès !");
        };
    }

    private Permission createPermissionIfNotFound(PermissionRepository repository, String name) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Permission(name)));
    }
}