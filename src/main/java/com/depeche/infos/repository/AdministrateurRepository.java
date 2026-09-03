package com.depeche.infos.repository;

import com.depeche.infos.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {

    // Recherche par email pour la connexion à l'espace d'administration
    Optional<Administrateur> findByEmail(String email);

    // Vérification de doublon d'email
    boolean existsByEmail(String email);

    // Récupérer les administrateurs actifs
    List<Administrateur> findByActifTrue();

    // Recherche par nom ou prénom
    List<Administrateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}