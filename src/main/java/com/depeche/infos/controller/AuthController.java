package com.depeche.infos.controller;


import com.depeche.infos.dto.AuthRequestDTO;
import com.depeche.infos.dto.AuthResponseDTO;
import com.depeche.infos.dto.InscriptionRequestDTO;
import com.depeche.infos.dto.MotDePasseOublieDTO;
import com.depeche.infos.dto.ReinitialisationMotDePasseDTO;
import com.depeche.infos.dto.UtilisateurResponseDTO;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.mapper.UtilisateurMapper;
import com.depeche.infos.security.AuthService;
import com.depeche.infos.service.MotDePasseOublieService;
import com.depeche.infos.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;
    private final MotDePasseOublieService motDePasseOublieService;

    public AuthController(AuthService authService, UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper,
                          MotDePasseOublieService motDePasseOublieService) {
        this.authService = authService;
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
        this.motDePasseOublieService = motDePasseOublieService;
    }

    //POST api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // POST api/auth/inscription : inscription publique d'un LECTEUR
    // (Correction de sécurité : avant, POST /api/utilisateurs était public et permettait de créer un ADMIN)
    @PostMapping("/inscription")
    public ResponseEntity<UtilisateurResponseDTO> inscription(@Valid @RequestBody InscriptionRequestDTO request) {
        Utilisateur lecteur = utilisateurService.creerUtilisateur(utilisateurMapper.toEntity(request));
        return ResponseEntity.status(201).body(utilisateurMapper.toResponseDTO(lecteur));
    }

    // POST api/auth/mot-de-passe-oublie : PUBLIC. Envoie un lien par e-mail (lecteurs et administrateurs seulement).
    // Réponse IDENTIQUE que le compte existe ou non : on ne révèle jamais quelles adresses ont un compte.
    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<Map<String, String>> motDePasseOublie(@Valid @RequestBody MotDePasseOublieDTO request) {
        motDePasseOublieService.demanderReinitialisation(request.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "Si un compte correspondant existe, un e-mail contenant un lien de réinitialisation vient d'être envoyé."));
    }

    // POST api/auth/reinitialiser-mot-de-passe : PUBLIC. Le jeton (reçu par e-mail) + le nouveau mot de passe.
    @PostMapping("/reinitialiser-mot-de-passe")
    public ResponseEntity<Void> reinitialiserMotDePasse(@Valid @RequestBody ReinitialisationMotDePasseDTO request) {
        motDePasseOublieService.reinitialiser(request.getJeton(), request.getMotDePasse());
        return ResponseEntity.noContent().build();
    }
}
