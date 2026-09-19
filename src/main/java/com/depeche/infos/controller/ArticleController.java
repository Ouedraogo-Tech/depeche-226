package com.depeche.infos.controller;

import com.depeche.infos.dto.ArticleRequestDTO;
import com.depeche.infos.dto.ArticleResponseDTO;
import com.depeche.infos.entity.Article;
import com.depeche.infos.mapper.ArticleMapper;
import com.depeche.infos.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    // On injecte le Service ET le Mapper
    public ArticleController(ArticleService articleService, ArticleMapper articleMapper) {
        this.articleService = articleService;
        this.articleMapper = articleMapper;
    }

    // --- LES OPÉRATIONS CRUD ---

    // POST /api/articles?auteurId=1 -> Créer un article
    @PostMapping
    public ResponseEntity<ArticleResponseDTO> creerArticle(@Valid @RequestBody ArticleRequestDTO request, @RequestParam Long auteurId) {
        // 1. On convertit le RequestDTO en Entité
        Article article = articleMapper.toEntity(request);

        // 2. Le service gère la création (statut, date, auteur, categorie)
        Article nouvelArticle = articleService.creeArticle(article, auteurId, request.getCategorieId());

        // 3. On convertit l'entité sauvegardée en ResponseDTO pour Postman
        return ResponseEntity.status(201).body(articleMapper.toResponseDTO(nouvelArticle));
    }

    // GET /api/articles -> Lister tous les articles
    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> listerArticles() {
        List<Article> articles = articleService.listerTousLesArticles();

        // On transforme la liste d'entités en liste de DTOs
        List<ArticleResponseDTO> dtos = articles.stream()
                .map(articleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // GET /api/articles/1 -> Afficher un article par son ID
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> obtenirArticleParId(@PathVariable Long id) {
        return articleService.rechercherArticleParId(id)
                .map(article -> ResponseEntity.ok(articleMapper.toResponseDTO(article)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/articles/1 -> Modifier un article
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> modifierArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequestDTO request) {
        Article articleModifie = articleMapper.toEntity(request);
        Article articleMisAJour = articleService.modifierArticle(id, articleModifie);
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleMisAJour));
    }

    // DELETE /api/articles/1 -> Supprimer un article
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerArticle(@PathVariable Long id) {
        articleService.supprimerArticle(id);
        return ResponseEntity.noContent().build();
    }

    // --- LA GESTION DES STATUTS ---

    @PatchMapping("/{id}/brouillon")
    public ResponseEntity<ArticleResponseDTO> mettreEnBrouillon(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.mettreEnBrouillons(id)));
    }

    @PatchMapping("/{id}/soumettre")
    public ResponseEntity<ArticleResponseDTO> soumettreArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.soumettreArticle(id)));
    }

    @PatchMapping("/{id}/publier")
    public ResponseEntity<ArticleResponseDTO> publierArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.publierArticle(id)));
    }

    @PatchMapping("/{id}/planifier")
    public ResponseEntity<ArticleResponseDTO> planifierArticle(@PathVariable Long id, @RequestParam String date) {
        LocalDateTime datePublication = LocalDateTime.parse(date);
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.planifierArticle(id, datePublication)));
    }

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<ArticleResponseDTO> archiverArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.archiverArticle(id)));
    }
}