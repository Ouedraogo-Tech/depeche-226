package com.depeche.infos.repository;

import com.depeche.infos.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // --- CÔTÉ PUBLIC (LECTEURS) ---

    // 1. Lister tous les articles publiés, du plus récent au plus ancien
    List<Article> findByStatutOrderByDatePublicationDesc(String statut);

    // 2. Filtrer les articles publiés par catégorie
    List<Article> findByCategorieIdAndStatutOrderByDatePublicationDesc(Long categorieId, String statut);

    // 3. Recherche par mot-clé dans le titre ou le contenu (uniquement parmi les articles publiés)
    List<Article> findByStatutAndTitreContainingIgnoreCaseOrStatutAndContenuContainingIgnoreCaseOrderByDatePublicationDesc(
            String statutTitre, String titre, String statutContenu, String contenu
    );


    // --- CÔTÉ JOURNALISTE / RÉDACTION ---

    // 4. Récupérer tous les articles écrits par un auteur spécifique (journaliste)
    List<Article> findByAuteurIdOrderByDateCreationDesc(Long auteurId);

    // 5. Filtrer les articles d'un auteur par statut (ex: voir ses brouillons ou articles en attente)
    List<Article> findByAuteurIdAndStatutOrderByDateCreationDesc(Long auteurId, String statut);


    // --- CÔTÉ ÉDITION / ADMINISTRATION ---

    // 6. Récupérer tous les articles selon leur statut (ex: "EN_ATTENTE_VALIDATION" pour le Responsable Éditorial)

    List<Article> findByStatutOrderByDateCreationDesc(String statut);

    // 7. Compter le nombre d'articles rédigés par catégorie (utile pour les statistiques)
    long countByCategorieId(Long categorieId);
}