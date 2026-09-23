package com.depeche.infos.controller;

import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.dto.CategorieResponseDTO;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.mapper.CategorieMapper;
import com.depeche.infos.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;
    private final CategorieMapper categorieMapper;

    public CategorieController(CategorieService categorieService, CategorieMapper categorieMapper) {
        this.categorieService = categorieService;
        this.categorieMapper = categorieMapper;
    }

    // POST /api/categories : Créer une catégorie
    @PreAuthorize("hasAuthority('CATEGORIE_CREER')")
    @PostMapping
    public ResponseEntity<CategorieResponseDTO> creerCategorie(@Valid @RequestBody CategorieRequestDTO request) {
        Categorie nouvelleCategorie = categorieService.creerCategorie(request);
        return ResponseEntity.status(201).body(categorieMapper.toResponseDTO(nouvelleCategorie));
    }

    // GET /api/categories :Lister toutes les catégories
    @GetMapping
    public ResponseEntity<List<CategorieResponseDTO>> listerCategories() {
        List<Categorie> categories = categorieService.listerToutesLesCategories();
        List<CategorieResponseDTO> dtos = categories.stream()
                .map(categorieMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/categories/1 : Afficher une catégorie par son ID
    @GetMapping("/{id}")
    public ResponseEntity<CategorieResponseDTO> obtenirCategorieParId(@PathVariable Long id) {
        Categorie categorie = categorieService.trouverCategorieParId(id);
        return ResponseEntity.ok(categorieMapper.toResponseDTO(categorie));
    }



    // PUT /api/categories/1 : Modifier une catégorie
    @PreAuthorize("hasAuthority('CATEGORIE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<CategorieResponseDTO> modifierCategorie(@PathVariable Long id, @Valid @RequestBody CategorieRequestDTO request) {
        Categorie categorieModifiee = categorieService.modifierCategorie(id, request);
        return ResponseEntity.ok(categorieMapper.toResponseDTO(categorieModifiee));
    }

    // DELETE /api/categories/1 :Supprimer une catégorie
    @PreAuthorize("hasAuthority('CATEGORIE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCategorie(@PathVariable Long id) {
        categorieService.supprimerCategorie(id);
        return ResponseEntity.noContent().build();
    }
}