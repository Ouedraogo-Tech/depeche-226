package com.depeche.infos.controller;

import com.depeche.infos.entity.Journaliste;
import com.depeche.infos.service.JournalisteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journalistes")
public class JournalisteController {

    private final JournalisteService journalisteService;

    public JournalisteController(JournalisteService journalisteService) {
        this.journalisteService = journalisteService;
    }

    @PostMapping
    public ResponseEntity<Journaliste> creer(@RequestBody Journaliste journaliste) {
        Journaliste nouveauJournaliste = journalisteService.creerJournaliste(journaliste);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauJournaliste);
    }
}