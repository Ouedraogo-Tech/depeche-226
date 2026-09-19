package com.depeche.infos.repository;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 1. Récupérer les articles par statut (ex: "PUBLIE" pour les lecteurs, "BROUILLON" pour les journalistes)
    List<Article> findByStatut(String statut);

    // 2. Récupérer les articles d'une catégorie spécifique
    List<Article> findByCategorie(Categorie categorie);

    // 3. Récupérer les articles écrits par un utilisateur (auteur) spécifique
    List<Article> findByAuteur(Utilisateur auteur);

    // 4. Récupérer les articles d'une catégorie ET d'un statut spécifique
    // (ex: Tous les articles PUBLIES de la catégorie SPORT)
    List<Article> findByCategorieAndStatut(Categorie categorie, String statut);

    // 5. Recherche par mot-clé dans le titre (comme on a vu pour l'utilisateur)
    List<Article> findByTitreContaining(String motCle);
}