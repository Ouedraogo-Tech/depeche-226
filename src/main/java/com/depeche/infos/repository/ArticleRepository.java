package com.depeche.infos.repository;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 1. Récupérer les articles par statut (ex: PUBLIE pour les lecteurs, SOUMIS pour le responsable éditorial)
    // Correction : le paramètre est maintenant l'enum StatutArticle (et non un String)
    List<Article> findByStatut(StatutArticle statut);

    // 1bis. Même chose, triés (les plus récents en premier)
    List<Article> findByStatutOrderByDatePublicationDesc(StatutArticle statut);

    List<Article> findByStatutOrderByDateCreationDesc(StatutArticle statut);

    // 2. Récupérer les articles d'une catégorie spécifique
    List<Article> findByCategorie(Categorie categorie);

    // 2bis. Savoir si une catégorie est utilisée (avant de la supprimer)
    boolean existsByCategorie(Categorie categorie);

    // 3. Récupérer les articles écrits par un utilisateur (auteur) spécifique
    List<Article> findByAuteur(Utilisateur auteur);

    List<Article> findByAuteurOrderByDateCreationDesc(Utilisateur auteur);

    // 4. Récupérer les articles d'une catégorie ET d'un statut spécifique
    // (ex: Tous les articles PUBLIES de la catégorie SPORT)
    List<Article> findByCategorieAndStatut(Categorie categorie, StatutArticle statut);

    // 5. Recherche par mot-clé dans le titre
    List<Article> findByTitreContaining(String motCle);

    // 6. Articles planifiés dont la date de publication est atteinte
    List<Article> findByStatutAndDatePublicationLessThanEqual(StatutArticle statut, LocalDateTime date);

    // 7. Statistiques
    long countByStatut(StatutArticle statut);
}
