package com.depeche.infos.controller;


import com.depeche.infos.dto.UtilisateurRequestDTO;
import com.depeche.infos.dto.UtilisateurResponseDTO;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.mapper.UtilisateurMapper;
import com.depeche.infos.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurController(UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
    }

    @PostMapping
    public ResponseEntity<UtilisateurResponseDTO> creerUtilisateur(@Valid @RequestBody UtilisateurRequestDTO request) {
        Utilisateur utilisateur = utilisateurMapper.toEntity(request);
        Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateur(utilisateur);
        return ResponseEntity.status(201).body(utilisateurMapper.toResponseDTO(nouvelUtilisateur));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('UTILISATEUR_CREER')")
    public ResponseEntity<List<UtilisateurResponseDTO>> listerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.listerTousLesUtilisateurs();
        List<UtilisateurResponseDTO> dtos = utilisateurs.stream()
                .map(utilisateurMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // PUT /api/utilisateurs/1 -> Modifier un utilisateur
    @PreAuthorize("hasAuthority('UTILISATEUR_CREER')")
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponseDTO> modifierUtilisateur(@PathVariable Long id, @Valid @RequestBody UtilisateurRequestDTO request) {
        // On convertit le DTO en entité partielle pour le service
        com.depeche.infos.entity.Utilisateur utilisateurModifie = new com.depeche.infos.entity.Journaliste(); // Création d'un objet temporaire
        utilisateurModifie.setNom(request.getNom());
        utilisateurModifie.setPrenom(request.getPrenom());
        utilisateurModifie.setMotDePasse(request.getMotDePasse());

        com.depeche.infos.entity.Utilisateur userMisAJour = utilisateurService.modifierUtilisateur(id, utilisateurModifie);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(userMisAJour));
    }

    // PATCH /api/utilisateurs/1/suspendre -> Suspendre un compte
    @PreAuthorize("hasAuthority('UTILISATEUR_DESACTIVER_SUSPENDRE')")
    @PatchMapping("/{id}/suspendre")
    public ResponseEntity<UtilisateurResponseDTO> suspendreUtilisateur(@PathVariable Long id) {
        com.depeche.infos.entity.Utilisateur userSuspendu = utilisateurService.suspendreUtilisateur(id);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(userSuspendu));
    }

    // PATCH /api/utilisateurs/1/activer -> Réactiver un compte
    @PreAuthorize("hasAuthority('UTILISATEUR_DESACTIVER_SUSPENDRE')")
    @PatchMapping("/{id}/activer")
    public ResponseEntity<UtilisateurResponseDTO> activerUtilisateur(@PathVariable Long id) {
        com.depeche.infos.entity.Utilisateur userActive = utilisateurService.activerUtilisateur(id);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(userActive));
    }
}