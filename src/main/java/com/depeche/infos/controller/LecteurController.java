package com.depeche.infos.controller;

import com.depeche.infos.entity.Lecteur;
import com.depeche.infos.service.LecteurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lecteurs")
public class LecteurController {

    private final LecteurService lecteurService;

    public LecteurController(LecteurService lecteurService) {
        this.lecteurService = lecteurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<Lecteur> inscription(@RequestBody Lecteur lecteur) {
        Lecteur nouveauLecteur = lecteurService.creerLecteur(lecteur);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauLecteur);
    }
}