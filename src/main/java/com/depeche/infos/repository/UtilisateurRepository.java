package com.depeche.infos.repository;

import com.depeche.infos.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Recherche par email pour l'authentification (résultat unique)
    Optional<Utilisateur> findByEmail(String email);

    // Vérification de doublon d'email
    boolean existsByEmail(String email);

    // Récupérer uniquement les utilisateurs actifs
    List<Utilisateur> findByActifTrue();

    // -- RECHERCHE PAR NOM ET PRÉNOM --

    // Recherche exacte par nom (peut retourner plusieurs utilisateurs)
    List<Utilisateur> findByNom(String nom);

    // Recherche partielle par nom sans tenir compte des majuscules/minuscules (recommandée)
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);

    // Recherche combinée nom et prénom
    List<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);


}
