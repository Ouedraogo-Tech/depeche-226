package com.depeche.infos.repository;

import com.depeche.infos.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // 1. Cherche un utilisateur par son email
    Optional<Utilisateur> findByEmail(String email);

    // 2. Vérifie si un email existe déjà
    boolean existsByEmail(String email);

    // 3. Rechercher par nom (ex: "Dupont")
    List<Utilisateur> findByNom(String nom);

    // 3bis. Rechercher par une partie du nom (ex: "Henr" trouvera "Henry")
    List<Utilisateur> findByNomContaining(String motCle);

    // 4. Rechercher les utilisateurs ayant un rôle spécifique (ex: tous les "RESPONSABLE_EDITORIAL")
    // Correction : "roles" contient des entités Role, on filtre donc sur Role.name
    List<Utilisateur> findByRoles_Name(String nomRole);

    // 5. Statistiques : comptes actifs / désactivés
    long countByActif(boolean actif);
}
