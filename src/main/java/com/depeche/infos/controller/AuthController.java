package com.depeche.infos.controller;


import com.depeche.infos.dto.AuthRequestDTO;
import com.depeche.infos.dto.AuthResponseDTO;
import com.depeche.infos.dto.InscriptionRequestDTO;
import com.depeche.infos.dto.UtilisateurResponseDTO;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.mapper.UtilisateurMapper;
import com.depeche.infos.security.AuthService;
import com.depeche.infos.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;

    public AuthController(AuthService authService, UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper) {
        this.authService = authService;
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
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
}
