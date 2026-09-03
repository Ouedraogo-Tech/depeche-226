package com.depeche.infos.repository;

import com.depeche.infos.entity.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    //  CÔTÉ PUBLIC (LECTEURS)
    // Récupérer les commentaires publiés d'un article, du plus récent au plus ancien
    List<Commentaire> findByArticleIdAndStatutOrderByDateCreationDesc(Long articleId, String statut);

    // CÔTÉ WEBMASTER
    // Récupérer tous les commentaires d'un article (publiés ou masqués) pour modération
    List<Commentaire> findByArticleIdOrderByDateCreationDesc(Long articleId);

    // Filtrer par statut (ex: lister tous les commentaires "MASQUE")
    List<Commentaire> findByStatut(String statut);

    //  CÔTÉ UTILISATEUR / PROFIL
    // Historique des commentaires rédigés par un utilisateur spécifique
    List<Commentaire> findByAuteurIdOrderByDateCreationDesc(Long utilisateurId);
}