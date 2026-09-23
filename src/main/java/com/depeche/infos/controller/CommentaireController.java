package com.depeche.infos.controller;

import com.depeche.infos.dto.CommentaireRequestDTO;
import com.depeche.infos.dto.CommentaireResponseDTO;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.mapper.CommentaireMapper;
import com.depeche.infos.repository.UtilisateurRepository;
import com.depeche.infos.service.CommentaireService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commentaires")
public class CommentaireController {

    private final CommentaireService commentaireService;
    private final CommentaireMapper commentaireMapper;
    private final UtilisateurRepository utilisateurRepository;

    public CommentaireController(CommentaireService commentaireService, CommentaireMapper commentaireMapper, UtilisateurRepository utilisateurRepository) {
        this.commentaireService = commentaireService;
        this.commentaireMapper = commentaireMapper;
        this.utilisateurRepository = utilisateurRepository;
    }

    // POST /api/commentaires?articleId=1
    // On récupère l'auteur depuis le Token

    @PreAuthorize("hasAuthority('COMMENTAIRE_ECRIRE')")
    @PostMapping
    public ResponseEntity<CommentaireResponseDTO> ajouterCommentaire(
            @Valid @RequestBody CommentaireRequestDTO request,
            @RequestParam Long articleId) {

        // 1. On récupère l'email de la personne connectée
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailConnecte = userDetails.getUsername();

        // 2. On cherche l'utilisateur en base
        Utilisateur auteur = utilisateurRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur non trouvé"));

        // 3. On crée le commentaire
        Commentaire commentaire = commentaireMapper.toEntity(request);
        Commentaire nouveauCommentaire = commentaireService.ajouterCommentaire(commentaire, auteur.getId(), articleId);

        return ResponseEntity.status(201).body(commentaireMapper.toResponseDTO(nouveauCommentaire));
    }

    // GET /api/commentaires/article/1
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentaireResponseDTO>> listerCommentairesParArticle(@PathVariable Long articleId) {
        List<Commentaire> commentaires = commentaireService.listerCommentairesParArticle(articleId);
        List<CommentaireResponseDTO> dtos = commentaires.stream()
                .map(commentaireMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // PATCH /api/commentaires/1/masquer
    @PreAuthorize("hasAuthority('COMMENTAIRE_MASQUER')")
    @PatchMapping("/{id}/masquer")
    public ResponseEntity<CommentaireResponseDTO> masquerCommentaire(@PathVariable Long id) {
        Commentaire commentaireMasque = commentaireService.masquerCommentaire(id);
        return ResponseEntity.ok(commentaireMapper.toResponseDTO(commentaireMasque));
    }

    // DELETE /api/commentaires/1
    @PreAuthorize("hasAuthority('COMMENTAIRE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommentaire(@PathVariable Long id) {
        commentaireService.supprimerCommentaire(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/commentaires
    @GetMapping
    public ResponseEntity<List<CommentaireResponseDTO>> listerTousLesCommentaires() {
        List<Commentaire> commentaires = commentaireService.listerTousLesCommentaires();
        List<CommentaireResponseDTO> dtos = commentaires.stream()
                .map(commentaireMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}