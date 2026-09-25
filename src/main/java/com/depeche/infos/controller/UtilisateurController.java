package com.depeche.infos.controller;


import com.depeche.infos.dto.UtilisateurModificationDTO;
import com.depeche.infos.dto.UtilisateurRequestDTO;
import com.depeche.infos.dto.UtilisateurResponseDTO;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.mapper.UtilisateurMapper;
import com.depeche.infos.security.UtilisateurConnecte;
import com.depeche.infos.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurController(UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
    }

    // POST /api/utilisateurs -> Créer un compte (Admin uniquement)
    // Correction de sécurité : cette route était publique (n'importe qui pouvait créer un ADMIN).
    // L'inscription publique d'un lecteur se fait maintenant via POST /api/auth/inscription.
    @PreAuthorize("hasAuthority('UTILISATEUR_CREER')")
    @PostMapping
    public ResponseEntity<UtilisateurResponseDTO> creerUtilisateur(@Valid @RequestBody UtilisateurRequestDTO request) {
        Utilisateur utilisateur = utilisateurMapper.toEntity(request);
        Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateur(utilisateur);
        return ResponseEntity.status(201).body(utilisateurMapper.toResponseDTO(nouvelUtilisateur));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('UTILISATEUR_CREER')")
    public ResponseEntity<List<UtilisateurResponseDTO>> listerUtilisateurs() {
        List<UtilisateurResponseDTO> dtos = utilisateurService.listerTousLesUtilisateurs().stream()
                .map(utilisateurMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/utilisateurs/moi -> Mon profil (nom, prénom, rôles...) : tout utilisateur connecté
    @GetMapping("/moi")
    public ResponseEntity<UtilisateurResponseDTO> monProfil() {
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateurService.trouverParEmail(UtilisateurConnecte.email())));
    }

    // PUT /api/utilisateurs/moi -> Modifier mon profil (nom, prénom, téléphone, mot de passe)
    @PutMapping("/moi")
    public ResponseEntity<UtilisateurResponseDTO> modifierMonProfil(@Valid @RequestBody UtilisateurModificationDTO request) {
        Utilisateur misAJour = utilisateurService.modifierMonProfil(UtilisateurConnecte.email(), request);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(misAJour));
    }

    // PUT /api/utilisateurs/1 -> Modifier un utilisateur (Admin)
    // Correction : utilise un DTO dédié (le mot de passe et le type ne sont plus obligatoires)
    @PreAuthorize("hasAuthority('UTILISATEUR_CREER')")
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponseDTO> modifierUtilisateur(@PathVariable Long id, @Valid @RequestBody UtilisateurModificationDTO request) {
        Utilisateur userMisAJour = utilisateurService.modifierUtilisateur(id, request);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(userMisAJour));
    }

    // PATCH /api/utilisateurs/1/suspendre -> Suspendre un compte
    @PreAuthorize("hasAuthority('UTILISATEUR_DESACTIVER_SUSPENDRE')")
    @PatchMapping("/{id}/suspendre")
    public ResponseEntity<UtilisateurResponseDTO> suspendreUtilisateur(@PathVariable Long id) {
        Utilisateur userSuspendu = utilisateurService.suspendreUtilisateur(id, UtilisateurConnecte.email());
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(userSuspendu));
    }

    // PATCH /api/utilisateurs/1/activer -> Réactiver un compte
    @PreAuthorize("hasAuthority('UTILISATEUR_DESACTIVER_SUSPENDRE')")
    @PatchMapping("/{id}/activer")
    public ResponseEntity<UtilisateurResponseDTO> activerUtilisateur(@PathVariable Long id) {
        Utilisateur userActive = utilisateurService.activerUtilisateur(id);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(userActive));
    }

    // PATCH /api/utilisateurs/1/role?role=JOURNALISTE -> Attribuer un rôle
    @PreAuthorize("hasAuthority('ROLE_ATTRIBUER')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UtilisateurResponseDTO> attribuerRole(@PathVariable Long id, @RequestParam String role) {
        Utilisateur utilisateurMisAJour = utilisateurService.attribuerRole(id, role);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateurMisAJour));
    }
}
