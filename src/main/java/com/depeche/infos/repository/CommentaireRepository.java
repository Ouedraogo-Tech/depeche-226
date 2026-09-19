package com.depeche.infos.repository;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    // 1. Récupérer tous les commentaires d'un article donné
    List<Commentaire> findByArticle(Article article);

    // 2. Récupérer les commentaires d'un article selon leur statut
    // (ex: on n'affiche que les "PUBLIE" pour les lecteurs)
    List<Commentaire> findByArticleAndStatut(Article article, String statut);

    // 3. Récupérer tous les commentaires faits par un utilisateur
    // (utile pour voir l'historique d'un lecteur ou le bannir si besoin)
    List<Commentaire> findByAuteur(Utilisateur auteur);

    // 4. Récupérer tous les commentaires par statut sur tout le site
    // (utile pour le Webmaster : afficher tous les commentaires "MASQUE" ou "SUPPRIME")
    List<Commentaire> findByStatut(String statut);
}