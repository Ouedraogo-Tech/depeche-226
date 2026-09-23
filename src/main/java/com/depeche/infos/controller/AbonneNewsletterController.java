package com.depeche.infos.controller;

import com.depeche.infos.dto.AbonneNewsletterDTO;
import com.depeche.infos.entity.AbonneNewsletter;
import com.depeche.infos.service.AbonneNewsletterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newsletter")
public class AbonneNewsletterController {

    private final AbonneNewsletterService abonneService;

    public AbonneNewsletterController(AbonneNewsletterService abonneService) {
        this.abonneService = abonneService;
    }

    // POST /api/newsletter/abonner :Public (n'importe qui peut s'abonner)
    @PreAuthorize("permitAll()")
    @PostMapping("/abonner")
    public ResponseEntity<AbonneNewsletter> abonner(@Valid @RequestBody AbonneNewsletterDTO request) {
        AbonneNewsletter nouvelAbonne = abonneService.abonner(request.getEmail());
        return ResponseEntity.status(201).body(nouvelAbonne);
    }

    // POST /api/newsletter/desabonner : Public
    @PreAuthorize("permitAll()")
    @PostMapping("/desabonner")
    public ResponseEntity<Void> desabonner(@RequestParam String email) {
        abonneService.desabonner(email);
        return ResponseEntity.noContent().build();
    }

    // GET /api/newsletter : Réservé à l'Admin (pour envoyer la newsletter)
    @PreAuthorize("hasAuthority('NEWSLETTER_ABONNER')") // Seul l'admin a cette permission dans ton DataInitializer
    @GetMapping
    public ResponseEntity<List<AbonneNewsletter>> listerAbonnesActifs() {
        return ResponseEntity.ok(abonneService.listerAbonnesActifs());
    }
}