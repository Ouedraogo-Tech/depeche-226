package com.depeche.infos.service;

import com.depeche.infos.dto.UtilisateurModificationDTO;
import com.depeche.infos.entity.*;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.RoleRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Utilisateur> rechercherParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Utilisateur connecté (GET /api/utilisateurs/moi)
    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'email : " + email));
    }

    public List<Utilisateur> listerTousLesUtilisateurs() {
        return utilisateurRepository.findAll(Sort.by("nom", "prenom"));
    }

    // Liste des rôles et de leurs permissions (espace Administrateur)
    public List<Role> listerRoles() {
        return roleRepository.findAll(Sort.by("id"));
    }

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        // Correction : l'email est comparé sans tenir compte des majuscules / espaces
        utilisateur.setEmail(utilisateur.getEmail().trim().toLowerCase());
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalStateException("Cet email est déjà utilisé !");
        }

        // Règle métier 1 : On hache le mot de passe avant de sauvegarder
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        // Correction : la date d'inscription n'était jamais remplie
        utilisateur.setDateInscription(LocalDateTime.now());
        utilisateur.setActif(true);

        // Règle métier 2 : On cherche le rôle en base de données de façon explicite
        String nomRole;
        if (utilisateur instanceof Administrateur) {
            nomRole = "ADMIN";
        } else if (utilisateur instanceof Webmaster) {
            nomRole = "WEBMASTER";
        } else if (utilisateur instanceof ResponsableEditorial) {
            nomRole = "RESPONSABLE_EDITORIAL";
        } else if (utilisateur instanceof Journaliste) {
            nomRole = "JOURNALISTE";
        } else if (utilisateur instanceof Lecteur) {
            nomRole = "LECTEUR";
        } else {
            throw new IllegalArgumentException("Type d'utilisateur non supporté");
        }

        Role role = roleRepository.findByName(nomRole)
                .orElseThrow(() -> new RessourceIntrouvableException("Le rôle " + nomRole + " n'existe pas en base de données."));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);

        return utilisateurRepository.save(utilisateur);
    }

    // 4. Modifier un utilisateur (par l'Admin)
    // Correction : on reçoit un DTO dédié (avant, le contrôleur créait un faux "Journaliste" temporaire)
    // et le téléphone est maintenant aussi modifiable.
    public Utilisateur modifierUtilisateur(Long id, UtilisateurModificationDTO modification) {
        Utilisateur utilisateurExistant = trouverParId(id);
        return appliquerModification(utilisateurExistant, modification);
    }

    // 4bis. Modifier son propre profil (tout utilisateur connecté)
    public Utilisateur modifierMonProfil(String email, UtilisateurModificationDTO modification) {
        return appliquerModification(trouverParEmail(email), modification);
    }

    // 5. Suspendre un compte (Désactivation)
    // Correction : un administrateur ne peut pas suspendre son propre compte
    public Utilisateur suspendreUtilisateur(Long id, String emailConnecte) {
        Utilisateur utilisateur = trouverParId(id);
        if (utilisateur.getEmail().equalsIgnoreCase(emailConnecte)) {
            throw new IllegalStateException("Vous ne pouvez pas suspendre votre propre compte.");
        }
        utilisateur.setActif(false); // On met le statut à false
        return utilisateurRepository.save(utilisateur);
    }

    // 6. Réactiver un compte
    public Utilisateur activerUtilisateur(Long id) {
        Utilisateur utilisateur = trouverParId(id);
        utilisateur.setActif(true); // On remet le statut à true
        return utilisateurRepository.save(utilisateur);
    }

    // 7. Attribuer un rôle à un utilisateur (par l'Admin)
    // Correction : accepte "JOURNALISTE" comme "ROLE_JOURNALISTE" (même format que typeUtilisateur)
    public Utilisateur attribuerRole(Long id, String nomRole) {
        Utilisateur utilisateur = trouverParId(id);

        String nomNormalise = nomRole.trim().toUpperCase().replaceFirst("^ROLE_", "");
        Role role = roleRepository.findByName(nomNormalise)
                .orElseThrow(() -> new RessourceIntrouvableException("Le rôle " + nomNormalise + " n'existe pas en base de données."));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);
        return utilisateurRepository.save(utilisateur);
    }

    private Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'id : " + id));
    }

    private Utilisateur appliquerModification(Utilisateur utilisateur, UtilisateurModificationDTO modification) {
        utilisateur.setNom(modification.getNom());
        utilisateur.setPrenom(modification.getPrenom());
        utilisateur.setTelephone(modification.getTelephone());

        // Si un nouveau mot de passe est fourni, on le hache. Sinon on garde l'ancien.
        if (modification.getMotDePasse() != null && !modification.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(modification.getMotDePasse()));
        }
        return utilisateurRepository.save(utilisateur);
    }
}
