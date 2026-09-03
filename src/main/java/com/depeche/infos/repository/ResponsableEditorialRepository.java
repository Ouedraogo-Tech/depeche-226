package com.depeche.infos.repository;

import com.depeche.infos.entity.ResponsableEditorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsableEditorialRepository extends JpaRepository<ResponsableEditorial, Long> {

    // Recherche par email (hérité de Utilisateur)
    Optional<ResponsableEditorial> findByEmail(String email);


    // Vérification de doublon d'email
    boolean existsByEmail(String email);

    // Recherche par nom ou prénom
    List<ResponsableEditorial> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}