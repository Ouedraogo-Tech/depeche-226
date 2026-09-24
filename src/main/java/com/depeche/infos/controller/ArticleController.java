package com.depeche.infos.controller;

import com.depeche.infos.dto.ArticleRequestDTO;
import com.depeche.infos.dto.ArticleResponseDTO;
import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.mapper.ArticleMapper;
import com.depeche.infos.repository.UtilisateurRepository;
import com.depeche.infos.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;
    private final UtilisateurRepository utilisateurRepository;

    // On injecte le Service ET le Mapper
    public ArticleController(ArticleService articleService, ArticleMapper articleMapper, UtilisateurRepository utilisateurRepository) {
        this.articleService = articleService;
        this.articleMapper = articleMapper;
        this.utilisateurRepository = utilisateurRepository;
    }

    // LES OPÉRATIONS CRUD

    // POST /api/articles -> On ne demande plus auteurId dans l'URL !
    @PreAuthorize("hasAuthority('ARTICLE_ECRIRE')")
    @PostMapping
    public ResponseEntity<ArticleResponseDTO> creerArticle(@Valid @RequestBody ArticleRequestDTO request) {
        // 1. On récupère l'email du journaliste connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailConnecte = userDetails.getUsername();

        // 2. On cherche le journaliste en base
        Utilisateur auteur = utilisateurRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur non trouvé"));

        // 3. On crée l'article en utilisant l'ID de l'utilisateur connecté
        Article article = articleMapper.toEntity(request);
        Article nouvelArticle = articleService.creeArticle(article, auteur.getId(), request.getCategorieId());

        return ResponseEntity.status(201).body(articleMapper.toResponseDTO(nouvelArticle));
    }

    // GET /api/articles :Lister tous les articles
    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> listerArticles() {
        List<Article> articles = articleService.listerTousLesArticles();

        // On transforme la liste d'entités en liste de DTOs
        List<ArticleResponseDTO> dtos = articles.stream()
                .map(articleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // GET /api/articles/1 :Afficher un article par son ID
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> obtenirArticleParId(@PathVariable Long id) {
        return articleService.rechercherArticleParId(id)
                .map(article -> ResponseEntity.ok(articleMapper.toResponseDTO(article)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/articles/1 -> Modifier un article
    @PreAuthorize("hasAuthority('ARTICLE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> modifierArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequestDTO request) {
        Article articleModifie = articleMapper.toEntity(request);
        Article articleMisAJour = articleService.modifierArticle(id, articleModifie);
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleMisAJour));
    }

    // DELETE /api/articles/1 :Supprimer un article
    @PreAuthorize("hasAuthority('ARTICLE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerArticle(@PathVariable Long id) {
        articleService.supprimerArticle(id);
        return ResponseEntity.noContent().build();
    }

    //LA GESTION DES STATUTS
    @PreAuthorize("hasAuthority('ARTICLE_BROUILLON_ENREGISTRER')")
    @PatchMapping("/{id}/brouillon")
    public ResponseEntity<ArticleResponseDTO> mettreEnBrouillon(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.mettreEnBrouillons(id)));
    }

    @PreAuthorize("hasAuthority('ARTICLE_SOUMETTRE')")
    @PatchMapping("/{id}/soumettre")
    public ResponseEntity<ArticleResponseDTO> soumettreArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.soumettreArticle(id)));
    }

    @PreAuthorize("hasAuthority('ARTICLE_PUBLIER')")
    @PatchMapping("/{id}/publier")
    public ResponseEntity<ArticleResponseDTO> publierArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.publierArticle(id)));
    }

    @PreAuthorize("hasAuthority('ARTICLE_PLANIFIER')")
    @PatchMapping("/{id}/planifier")
    public ResponseEntity<ArticleResponseDTO> planifierArticle(@PathVariable Long id, @RequestParam String date) {
        LocalDateTime datePublication = LocalDateTime.parse(date);
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.planifierArticle(id, datePublication)));
    }

    @PreAuthorize("hasAuthority('ARTICLE_PUBLIER')")
    @PatchMapping("/{id}/archiver")
    public ResponseEntity<ArticleResponseDTO> archiverArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.archiverArticle(id)));
    }

        // GET /api/articles/moi -> Lister uniquement mes propres articles (Journaliste connecté)
    @PreAuthorize("hasAuthority('ARTICLE_ECRIRE')")
    @GetMapping("/moi")
    public ResponseEntity<List<ArticleResponseDTO>> mesArticles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailConnecte = userDetails.getUsername();

        List<Article> articles = articleService.listerArticlesParAuteur(emailConnecte);
        List<ArticleResponseDTO> dtos = articles.stream()
                .map(articleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}