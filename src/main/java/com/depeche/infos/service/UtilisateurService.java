package com.depeche.infos.service;


import com.depeche.infos.entity.Role;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.RoleRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Pour hacher le mot de passe

    // On injecte les 3 dépendances
    public UtilisateurService(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Méthode pour chercher un utilisateur par email
    public Optional<Utilisateur> rechercherParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // 2. Méthode pour lister tous les utilisateurs
    public List<Utilisateur> listerTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // 3. Méthode pour sauvegarder/créer un utilisateur
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé !");
        }

        // Règle métier 1 : On hache le mot de passe avant de sauvegarder
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        // Règle métier 2 : On cherche le rôle en base de données
        // On déduit le nom du rôle à partir du type de l'instance (ex: Journaliste -> "JOURNALISTE")
        String nomRole = utilisateur.getClass().getSimpleName().toUpperCase();
        Role role = roleRepository.findByName(nomRole)
                .orElseThrow(() -> new RessourceIntrouvableException("Le rôle " + nomRole + " n'existe pas en base de données."));

        // On attribue le rôle à l'utilisateur
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);

        return utilisateurRepository.save(utilisateur);
    }
}
