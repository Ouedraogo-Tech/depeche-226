package com.depeche.infos.controller;


import com.depeche.infos.dto.CommentaireRequestDTO;
import com.depeche.infos.dto.CommentaireResponseDTO;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.mapper.CommentaireMapper;
import com.depeche.infos.service.CommentaireService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commentaires")
public class CommentaireController {

    private final CommentaireService commentaireService;
    private final CommentaireMapper commentaireMapper;

    public CommentaireController(CommentaireService commentaireService, CommentaireMapper commentaireMapper) {
        this.commentaireService = commentaireService;
        this.commentaireMapper = commentaireMapper;
    }

    // POST /api/commentaires?articleId=1&auteurId=2
    @PreAuthorize("hasAuthority('COMMENTAIRE_ECRIRE')")
    @PostMapping
    public ResponseEntity<CommentaireResponseDTO> ajouterCommentaire(
            @Valid @RequestBody CommentaireRequestDTO request,
            @RequestParam Long articleId,
            @RequestParam Long auteurId) {

        Commentaire commentaire = commentaireMapper.toEntity(request);
        Commentaire nouveauCommentaire = commentaireService.ajouterCommentaire(commentaire, auteurId, articleId);
        return ResponseEntity.status(201).body(commentaireMapper.toResponseDTO(nouveauCommentaire));
    }

    // GET /api/commentaires/article/1 -> Lister les commentaires de l'article 1
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentaireResponseDTO>> listerCommentairesParArticle(@PathVariable Long articleId) {
        List<Commentaire> commentaires = commentaireService.listerCommentairesParArticle(articleId);
        List<CommentaireResponseDTO> dtos = commentaires.stream()
                .map(commentaireMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // PATCH /api/commentaires/1/masquer -> Modérer un commentaire
    @PreAuthorize("hasAuthority('COMMENTAIRE_MASQUER')")
    @PatchMapping("/{id}/masquer")
    public ResponseEntity<CommentaireResponseDTO> masquerCommentaire(@PathVariable Long id) {
        Commentaire commentaireMasque = commentaireService.masquerCommentaire(id);
        return ResponseEntity.ok(commentaireMapper.toResponseDTO(commentaireMasque));
    }

    // GET /api/commentaires -> Lister tous les commentaires
    @GetMapping
    public ResponseEntity<List<CommentaireResponseDTO>> listerTousLesCommentaires() {
        List<Commentaire> commentaires = commentaireService.listerTousLesCommentaires();
        List<CommentaireResponseDTO> dtos = commentaires.stream()
                .map(commentaireMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    //DELETE /api/commentaires/1 -> Supprimer définitivement un commentaire
    @PreAuthorize("hasAuthority('COMMENTAIRE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommentaire(@PathVariable Long id) {
        commentaireService.supprimerCommentaire(id);
        return ResponseEntity.noContent().build(); // 204 No Content (suppression réussie)
    }
}
