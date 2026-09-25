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

    // POST /api/newsletter/abonner : Public (n'importe qui peut s'abonner)
    // Correction : la route est maintenant réellement publique dans SecurityConfig
    // (@PreAuthorize("permitAll()") ne suffisait pas : anyRequest().authenticated() bloquait avant)
    @PostMapping("/abonner")
    public ResponseEntity<AbonneNewsletter> abonner(@Valid @RequestBody AbonneNewsletterDTO request) {
        AbonneNewsletter nouvelAbonne = abonneService.abonner(request.getEmail());
        return ResponseEntity.status(201).body(nouvelAbonne);
    }

    // POST /api/newsletter/desabonner?email=... : Public (lien de désabonnement) et Webmaster
    @PostMapping("/desabonner")
    public ResponseEntity<Void> desabonner(@RequestParam String email) {
        abonneService.desabonner(email);
        return ResponseEntity.noContent().build();
    }

    // GET /api/newsletter?actifsSeulement=true : liste des abonnés (Webmaster)
    // Correction : exigeait NEWSLETTER_ABONNER, que seul le LECTEUR possédait.
    // La nouvelle permission NEWSLETTER_GERER est donnée au WEBMASTER.
    @PreAuthorize("hasAuthority('NEWSLETTER_GERER')")
    @GetMapping
    public ResponseEntity<List<AbonneNewsletter>> listerAbonnes(@RequestParam(defaultValue = "false") boolean actifsSeulement) {
        return ResponseEntity.ok(actifsSeulement
                ? abonneService.listerAbonnesActifs()
                : abonneService.listerTousLesAbonnes());
    }
}
