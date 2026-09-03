package com.depeche.infos.repository;

import com.depeche.infos.entity.Webmaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebmasterRepository extends JpaRepository<Webmaster, Long> {

    // Recherche par email (hérité de Utilisateur)
    Optional<Webmaster> findByEmail(String email);

    // Vérification de doublon d'email
    boolean existsByEmail(String email);

    // Recherche par nom ou prénom
    List<Webmaster> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}