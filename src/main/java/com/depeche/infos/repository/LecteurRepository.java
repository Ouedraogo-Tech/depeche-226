package com.depeche.infos.repository;

import com.depeche.infos.entity.Lecteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecteurRepository extends JpaRepository<Lecteur, Long> {

    // Recherche par email pour l'authentification et l'espace lecteur
    Optional<Lecteur> findByEmail(String email);

    // Vérification de doublon d'email à la création du compte lecteur
    boolean existsByEmail(String email);

    // Recherche par nom ou prénom (pour l'administration ou la modération)
    List<Lecteur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}