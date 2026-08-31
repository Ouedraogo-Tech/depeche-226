package com.depeche.infos.controller;

import com.depeche.infos.entity.Administrateur;
import com.depeche.infos.service.AdministrateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/administrateurs")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    public AdministrateurController(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    @PostMapping
    public ResponseEntity<Administrateur> creer(@RequestBody Administrateur administrateur) {
        Administrateur nouveauAdministrateur = administrateurService.creerAdministrateur(administrateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauAdministrateur);
    }
}
