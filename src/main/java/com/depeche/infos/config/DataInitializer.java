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
            System.out.println(">>> Initialisation des rôles et permissions (Basé sur le diagramme UML)...");

            // 1. CRÉATION DES PERMISSIONS (Les bulles du diagramme)
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
            Permission planifierPublications = createPermissionIfNotFound(permissionRepository, "ARTICLE_PLANIFIER");
            Permission supprimerArticle = createPermissionIfNotFound(permissionRepository, "ARTICLE_SUPPRIMER");
            Permission gererRubriques = createPermissionIfNotFound(permissionRepository, "RUBRIQUE_GERER");
            Permission suivreStats = createPermissionIfNotFound(permissionRepository, "STATISTIQUES_SUIVRE");
            Permission gererCommentaires = createPermissionIfNotFound(permissionRepository, "COMMENTAIRE_GERER");
            Permission gererNotifications = createPermissionIfNotFound(permissionRepository, "NOTIFICATION_GERER");

            Permission creerComptes = createPermissionIfNotFound(permissionRepository, "UTILISATEUR_CREER");
            Permission attribuerRoles = createPermissionIfNotFound(permissionRepository, "ROLE_ATTRIBUER");
            Permission desactiverComptes = createPermissionIfNotFound(permissionRepository, "UTILISATEUR_DESACTIVER_SUPPRIMER");

            // 2. ATTRIBUTION DES PERMISSIONS AUX RÔLES (Les acteurs du diagramme)

            // RÔLE LECTEUR
            if (!roleRepository.findByName("LECTEUR").isPresent()) {
                Role lecteur = new Role("LECTEUR");
                Set<Permission> permsLecteur = new HashSet<>(Arrays.asList(
                        consulterActualites, lireArticles, mettreFavoris, consulterCategories, commenterArticles, abonnerNewsletter
                ));
                lecteur.setPermissions(permsLecteur);
                roleRepository.save(lecteur);
            }

            // RÔLE JOURNALISTE
            if (!roleRepository.findByName("JOURNALISTE").isPresent()) {
                Role journaliste = new Role("JOURNALISTE");
                Set<Permission> permsJournaliste = new HashSet<>(Arrays.asList(
                        redigerArticle, modifierArticle, publierArticle, soumettreArticle, consulterActualites, supprimerArticle
                ));
                journaliste.setPermissions(permsJournaliste);
                roleRepository.save(journaliste);
            }

            // RÔLE RESPONSABLE EDITORIAL
            if (!roleRepository.findByName("RESPONSABLE_EDITORIAL").isPresent()) {
                Role responsable = new Role("RESPONSABLE_EDITORIAL");
                Set<Permission> permsResponsable = new HashSet<>(Arrays.asList(
                        redigerArticle, modifierArticle, enregistrerBrouillon, soumettreArticle,
                        supprimerArticle, planifierPublications, consulterActualites, gererRubriques, suivreStats
                ));
                responsable.setPermissions(permsResponsable);
                roleRepository.save(responsable);
            }

            // RÔLE WEBMASTER
            if (!roleRepository.findByName("WEBMASTER").isPresent()) {
                Role webmaster = new Role("WEBMASTER");
                Set<Permission> permsWebmaster = new HashSet<>(Arrays.asList(
                        suivreStats, gererCommentaires, gererNotifications
                ));
                webmaster.setPermissions(permsWebmaster);
                roleRepository.save(webmaster);
            }

            // RÔLE ADMIN (A TOUTES LES PERMISSIONS)
            if (!roleRepository.findByName("ADMIN").isPresent()) {
                Role admin = new Role("ADMIN");
                Set<Permission> allPerms = new HashSet<>(Arrays.asList(
                        consulterActualites, creerComptes, attribuerRoles, consulterCategories, desactiverComptes
                ));
                admin.setPermissions(allPerms);
                roleRepository.save(admin);
            }

            System.out.println(">>> Rôles et permissions initialisés avec succès !");
        };
    }

    private Permission createPermissionIfNotFound(PermissionRepository repository, String name) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Permission(name)));
    }
}