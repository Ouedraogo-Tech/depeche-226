package com.depeche.infos.controller;

import com.depeche.infos.entity.ResponsableEditorial;
import com.depeche.infos.service.ResponsableEditorialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/responsables-editoriaux")
public class ResponsableEditorialController {

    private final ResponsableEditorialService responsableEditorialService;

    public ResponsableEditorialController(ResponsableEditorialService responsableEditorialService) {
        this.responsableEditorialService = responsableEditorialService;
    }

    @PostMapping
    public ResponseEntity<ResponsableEditorial> creer(@RequestBody ResponsableEditorial responsableEditorial) {
        ResponsableEditorial nouveau = responsableEditorialService.creerResponsableEditorial(responsableEditorial);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveau);
    }
}