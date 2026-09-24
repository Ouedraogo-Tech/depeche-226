package com.depeche.infos.service;

import com.depeche.infos.entity.*;
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
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Utilisateur> rechercherParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public List<Utilisateur> listerTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé !");
        }

        // Règle métier 1 : On hache le mot de passe avant de sauvegarder
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

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
            throw new RuntimeException("Type d'utilisateur non supporté");
        }

        Role role = roleRepository.findByName(nomRole)
                .orElseThrow(() -> new RessourceIntrouvableException("Le rôle " + nomRole + " n'existe pas en base de données."));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);

        return utilisateurRepository.save(utilisateur);
    }

    // 4. Modifier un utilisateur (par l'Admin)
    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurModifie) {
        Utilisateur utilisateurExistant = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'id : " + id));

        utilisateurExistant.setNom(utilisateurModifie.getNom());
        utilisateurExistant.setPrenom(utilisateurModifie.getPrenom());

        // Si l'admin fournit un nouveau mot de passe, on le hache. Sinon on garde l'ancien.
        if (utilisateurModifie.getMotDePasse() != null && !utilisateurModifie.getMotDePasse().isEmpty()) {
            utilisateurExistant.setMotDePasse(passwordEncoder.encode(utilisateurModifie.getMotDePasse()));
        }

        return utilisateurRepository.save(utilisateurExistant);
    }

    // 5. Suspendre un compte (Désactivation)
    public Utilisateur suspendreUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'id : " + id));

        utilisateur.setActif(false); // On met le statut à false
        return utilisateurRepository.save(utilisateur);
    }

    // 6. Réactiver un compte
    public Utilisateur activerUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'id : " + id));

        utilisateur.setActif(true); // On remet le statut à true
        return utilisateurRepository.save(utilisateur);
    }

        // 7. Attribuer un rôle à un utilisateur (par l'Admin)
    public Utilisateur attribuerRole(Long id, String nomRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'id : " + id));

        Role role = roleRepository.findByName(nomRole)
                .orElseThrow(() -> new RessourceIntrouvableException("Le rôle " + nomRole + " n'existe pas en base de données."));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);

        return utilisateurRepository.save(utilisateur);
    }
}