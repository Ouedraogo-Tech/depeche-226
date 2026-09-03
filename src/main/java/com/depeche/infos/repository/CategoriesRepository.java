package com.depeche.infos.repository;

import com.depeche.infos.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    // Vérifie l'existence par nom exact
    boolean existsByNom(String nom);

    // Recherche par nom exact
    Optional<Categories> findByNom(String nom);

    // -- AMÉLIORATIONS RECOMMANDÉES --

    // Vérifie l'existence sans tenir compte des majuscules/minuscules
    boolean existsByNomIgnoreCase(String nom);

    // Recherche par nom sans tenir compte des majuscules/minuscules
    Optional<Categories> findByNomIgnoreCase(String nom);

    // Remarque : findById(Long id) est déjà inclus par JpaRepository !
}