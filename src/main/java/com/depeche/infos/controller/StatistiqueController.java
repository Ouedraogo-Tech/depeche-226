package com.depeche.infos.controller;

import com.depeche.infos.dto.StatistiquesDTO;
import com.depeche.infos.service.StatistiqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistiques")
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    // GET /api/statistiques : chiffres des dashboards (Admin, Responsable éditorial, Webmaster)
    @PreAuthorize("hasAuthority('STATISTIQUES_SUIVRE')")
    @GetMapping
    public ResponseEntity<StatistiquesDTO> obtenirStatistiques() {
        return ResponseEntity.ok(statistiqueService.calculerStatistiques());
    }
}
