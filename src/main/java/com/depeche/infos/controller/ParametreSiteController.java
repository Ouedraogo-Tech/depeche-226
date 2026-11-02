package com.depeche.infos.controller;

import com.depeche.infos.dto.AProposDTO;
import com.depeche.infos.dto.ParametreSiteDTO;
import com.depeche.infos.mapper.ParametreSiteMapper;
import com.depeche.infos.service.ParametreSiteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Paramètres du footer (nom du site, slogan, contact...).
@RestController
@RequestMapping("/api/parametres")
public class ParametreSiteController {

    private final ParametreSiteService parametreSiteService;
    private final ParametreSiteMapper parametreSiteMapper;

    public ParametreSiteController(ParametreSiteService parametreSiteService, ParametreSiteMapper parametreSiteMapper) {
        this.parametreSiteService = parametreSiteService;
        this.parametreSiteMapper = parametreSiteMapper;
    }

    // GET /api/parametres : PUBLIC (le footer du site public en a besoin)
    @GetMapping
    public ResponseEntity<ParametreSiteDTO> obtenirParametres() {
        return ResponseEntity.ok(parametreSiteMapper.toDTO(parametreSiteService.obtenirParametres()));
    }

    // PUT /api/parametres : Webmaster uniquement
    @PreAuthorize("hasAuthority('PARAMETRES_GERER')")
    @PutMapping
    public ResponseEntity<ParametreSiteDTO> modifierParametres(@Valid @RequestBody ParametreSiteDTO request) {
        return ResponseEntity.ok(parametreSiteMapper.toDTO(parametreSiteService.modifierParametres(request)));
    }

    // GET /api/parametres/a-propos : PUBLIC (page "À propos" du site)
    @GetMapping("/a-propos")
    public ResponseEntity<AProposDTO> obtenirAPropos() {
        return ResponseEntity.ok(parametreSiteMapper.toAProposDTO(parametreSiteService.obtenirParametres()));
    }

    // PUT /api/parametres/a-propos : Webmaster uniquement
    @PreAuthorize("hasAuthority('PARAMETRES_GERER')")
    @PutMapping("/a-propos")
    public ResponseEntity<AProposDTO> modifierAPropos(@Valid @RequestBody AProposDTO request) {
        return ResponseEntity.ok(parametreSiteMapper.toAProposDTO(parametreSiteService.modifierAPropos(request)));
    }
}
