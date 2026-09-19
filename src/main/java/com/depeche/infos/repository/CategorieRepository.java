package com.depeche.infos.repository;

import com.depeche.infos.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    // 1. Chercher une catégorie par son nom (ex: "Sport", "Économie")
    // On retourne un Optional car la catégorie pourrait ne pas exister
    Optional<Categorie> findByNom(String nom);

    // 2. Vérifier si une catégorie existe déjà par son nom
    // Utile quand l'administrateur veut créer une nouvelle catégorie
    boolean existsByNom(String nom);
}