package com.depeche.infos.repository;

import com.depeche.infos.entity.Journaliste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalisteRepository extends JpaRepository<Journaliste, Long> {

    // Recherche par email pour l'authentification / profil
    Optional<Journaliste> findByEmail(String email);

    // Vérification d'existence d'email
    boolean existsByEmail(String email);

    // Récupérer uniquement les journalistes actifs sur la plateforme
    List<Journaliste> findByActifTrue();

    // Recherche par nom ou prénom
    List<Journaliste> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}