package com.depeche.infos.repository;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    // 1. Récupérer tous les commentaires d'un article donné
    List<Commentaire> findByArticle(Article article);

    // 2. Récupérer les commentaires d'un article selon leur statut
    // (ex: on n'affiche que les "PUBLIE" pour les lecteurs)
    // Correction : le paramètre est maintenant l'enum StatutCommentaire (et non un String)
    List<Commentaire> findByArticleAndStatutOrderByDateCreationDesc(Article article, StatutCommentaire statut);

    // 3. Récupérer tous les commentaires faits par un utilisateur
    List<Commentaire> findByAuteur(Utilisateur auteur);

    // 4. Récupérer tous les commentaires par statut sur tout le site (utile pour le Webmaster)
    List<Commentaire> findByStatutOrderByDateCreationDesc(StatutCommentaire statut);

    List<Commentaire> findAllByOrderByDateCreationDesc();

    // 5. Commentaires reçus sur les articles d'un journaliste
    List<Commentaire> findByArticle_AuteurOrderByDateCreationDesc(Utilisateur auteur);

    // 6. Supprimer les commentaires d'un article (avant de supprimer l'article lui-même)
    void deleteByArticle(Article article);

    // 7. Statistiques
    long countByStatut(StatutCommentaire statut);
}
