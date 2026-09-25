package com.depeche.infos.controller;

import com.depeche.infos.dto.CommentaireRequestDTO;
import com.depeche.infos.dto.CommentaireResponseDTO;
import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutCommentaire;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.mapper.CommentaireMapper;
import com.depeche.infos.repository.UtilisateurRepository;
import com.depeche.infos.security.UtilisateurConnecte;
import com.depeche.infos.service.CommentaireService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        Utilisateur auteur = utilisateurRepository.findByEmail(UtilisateurConnecte.email())
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur non trouvé"));

        Commentaire commentaire = commentaireMapper.toEntity(request);
        Commentaire nouveauCommentaire = commentaireService.ajouterCommentaire(commentaire, auteur.getId(), articleId);

        return ResponseEntity.status(201).body(commentaireMapper.toResponseDTO(nouveauCommentaire));
    }

    // GET /api/commentaires/article/1 : PUBLIC, uniquement les commentaires publiés
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentaireResponseDTO>> listerCommentairesParArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(versDTOs(commentaireService.listerCommentairesParArticle(articleId)));
    }

    // GET /api/commentaires/mes-articles : commentaires reçus sur mes articles (Journaliste)
    @PreAuthorize("hasAuthority('ARTICLE_ECRIRE')")
    @GetMapping("/mes-articles")
    public ResponseEntity<List<CommentaireResponseDTO>> listerCommentairesSurMesArticles() {
        return ResponseEntity.ok(versDTOs(commentaireService.listerCommentairesSurMesArticles(UtilisateurConnecte.email())));
    }

    // PATCH /api/commentaires/1/masquer
    @PreAuthorize("hasAuthority('COMMENTAIRE_MASQUER')")
    @PatchMapping("/{id}/masquer")
    public ResponseEntity<CommentaireResponseDTO> masquerCommentaire(@PathVariable Long id) {
        return ResponseEntity.ok(commentaireMapper.toResponseDTO(commentaireService.masquerCommentaire(id)));
    }

    // PATCH /api/commentaires/1/publier : rendre à nouveau visible un commentaire masqué
    @PreAuthorize("hasAuthority('COMMENTAIRE_PUBLIER')")
    @PatchMapping("/{id}/publier")
    public ResponseEntity<CommentaireResponseDTO> publierCommentaire(@PathVariable Long id) {
        return ResponseEntity.ok(commentaireMapper.toResponseDTO(commentaireService.publierCommentaire(id)));
    }

    // DELETE /api/commentaires/1
    @PreAuthorize("hasAuthority('COMMENTAIRE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommentaire(@PathVariable Long id) {
        commentaireService.supprimerCommentaire(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/commentaires?statut=MASQUE : modération (Webmaster)
    // Correction : réservé aux modérateurs (avant, n'importe quel connecté voyait les commentaires masqués)
    @PreAuthorize("hasAuthority('COMMENTAIRE_MASQUER')")
    @GetMapping
    public ResponseEntity<List<CommentaireResponseDTO>> listerTousLesCommentaires(
            @RequestParam(required = false) StatutCommentaire statut) {
        return ResponseEntity.ok(versDTOs(commentaireService.listerTousLesCommentaires(statut)));
    }

    private List<CommentaireResponseDTO> versDTOs(List<Commentaire> commentaires) {
        return commentaires.stream().map(commentaireMapper::toResponseDTO).toList();
    }
}
