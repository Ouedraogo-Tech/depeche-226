package com.depeche.infos.controller;

import com.depeche.infos.dto.ArticleRequestDTO;
import com.depeche.infos.dto.ArticleResponseDTO;
import com.depeche.infos.dto.DecisionEditorialeDTO;
import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.mapper.ArticleMapper;
import com.depeche.infos.repository.UtilisateurRepository;
import com.depeche.infos.security.UtilisateurConnecte;
import com.depeche.infos.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    // ==================== SITE PUBLIC (sans token) ====================

    // GET /api/articles/publies?categorieId=2&recherche=coton : articles PUBLIES uniquement
    @GetMapping("/publies")
    public ResponseEntity<List<ArticleResponseDTO>> listerArticlesPublies(
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) String recherche) {
        return ResponseEntity.ok(versDTOs(articleService.listerArticlesPublies(categorieId, recherche)));
    }

    // GET /api/articles/publies/1 : un article publié (404 s'il n'est pas publié)
    @GetMapping("/publies/{id}")
    public ResponseEntity<ArticleResponseDTO> obtenirArticlePublie(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.trouverArticlePublie(id)));
    }

    // ==================== BACK-OFFICE ====================

    // POST /api/articles -> l'auteur est l'utilisateur connecté (lu dans le token)
    @PreAuthorize("hasAuthority('ARTICLE_ECRIRE')")
    @PostMapping
    public ResponseEntity<ArticleResponseDTO> creerArticle(@Valid @RequestBody ArticleRequestDTO request) {
        Utilisateur auteur = utilisateurRepository.findByEmail(UtilisateurConnecte.email())
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur non trouvé"));

        Article article = articleMapper.toEntity(request);
        Article nouvelArticle = articleService.creeArticle(article, auteur.getId(), request.getCategorieId());

        return ResponseEntity.status(201).body(articleMapper.toResponseDTO(nouvelArticle));
    }

    // GET /api/articles?statut=SOUMIS : tous les articles (tous statuts), filtre facultatif
    // Correction : réservé au back-office (avant, n'importe quel connecté voyait les brouillons)
    @PreAuthorize("hasAnyAuthority('ARTICLE_VALIDER', 'UTILISATEUR_CREER', 'STATISTIQUES_SUIVRE')")
    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> listerArticles(@RequestParam(required = false) StatutArticle statut) {
        List<Article> articles = (statut == null)
                ? articleService.listerTousLesArticles()
                : articleService.listerArticlesParStatut(statut);
        return ResponseEntity.ok(versDTOs(articles));
    }

    // GET /api/articles/moi -> Lister uniquement mes propres articles (Journaliste connecté)
    @PreAuthorize("hasAuthority('ARTICLE_ECRIRE')")
    @GetMapping("/moi")
    public ResponseEntity<List<ArticleResponseDTO>> mesArticles() {
        return ResponseEntity.ok(versDTOs(articleService.listerArticlesParAuteur(UtilisateurConnecte.email())));
    }

    // GET /api/articles/1 : un article quel que soit son statut (back-office)
    @PreAuthorize("hasAnyAuthority('ARTICLE_ECRIRE', 'UTILISATEUR_CREER', 'STATISTIQUES_SUIVRE')")
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> obtenirArticleParId(@PathVariable Long id) {
        return articleService.rechercherArticleParId(id)
                .map(article -> ResponseEntity.ok(articleMapper.toResponseDTO(article)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/articles/1 -> Modifier un article (son auteur ou un responsable éditorial)
    @PreAuthorize("hasAuthority('ARTICLE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> modifierArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequestDTO request) {
        Article articleModifie = articleMapper.toEntity(request);
        Article articleMisAJour = articleService.modifierArticle(id, articleModifie, request.getCategorieId(), UtilisateurConnecte.email());
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleMisAJour));
    }

    // DELETE /api/articles/1 :Supprimer un article (et ses commentaires)
    @PreAuthorize("hasAuthority('ARTICLE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerArticle(@PathVariable Long id) {
        articleService.supprimerArticle(id, UtilisateurConnecte.email());
        return ResponseEntity.noContent().build();
    }

    // ==================== GESTION DES STATUTS ====================

    @PreAuthorize("hasAuthority('ARTICLE_BROUILLON_ENREGISTRER')")
    @PatchMapping("/{id}/brouillon")
    public ResponseEntity<ArticleResponseDTO> mettreEnBrouillon(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.mettreEnBrouillons(id, UtilisateurConnecte.email())));
    }

    @PreAuthorize("hasAuthority('ARTICLE_SOUMETTRE')")
    @PatchMapping("/{id}/soumettre")
    public ResponseEntity<ArticleResponseDTO> soumettreArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.soumettreArticle(id, UtilisateurConnecte.email())));
    }

    @PreAuthorize("hasAuthority('ARTICLE_PUBLIER')")
    @PatchMapping("/{id}/publier")
    public ResponseEntity<ArticleResponseDTO> publierArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.publierArticle(id, UtilisateurConnecte.email())));
    }

    // PATCH /api/articles/1/planifier?date=2026-10-01T08:00:00
    @PreAuthorize("hasAuthority('ARTICLE_PLANIFIER')")
    @PatchMapping("/{id}/planifier")
    public ResponseEntity<ArticleResponseDTO> planifierArticle(@PathVariable Long id, @RequestParam String date) {
        LocalDateTime datePublication = LocalDateTime.parse(date);
        return ResponseEntity.ok(articleMapper.toResponseDTO(
                articleService.planifierArticle(id, datePublication, UtilisateurConnecte.email())));
    }

    @PreAuthorize("hasAuthority('ARTICLE_PUBLIER')")
    @PatchMapping("/{id}/archiver")
    public ResponseEntity<ArticleResponseDTO> archiverArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.archiverArticle(id, UtilisateurConnecte.email())));
    }

    // VALIDATION (Responsable éditorial) 
    // Corps facultatif : { "commentaire": "Retour pour le journaliste..." }

    @PreAuthorize("hasAuthority('ARTICLE_VALIDER')")
    @PatchMapping("/{id}/valider")
    public ResponseEntity<ArticleResponseDTO> validerArticle(@PathVariable Long id,
                                                             @Valid @RequestBody(required = false) DecisionEditorialeDTO decision) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.validerArticle(id, commentaire(decision))));
    }

    @PreAuthorize("hasAuthority('ARTICLE_VALIDER')")
    @PatchMapping("/{id}/demander-modification")
    public ResponseEntity<ArticleResponseDTO> demanderModification(@PathVariable Long id,
                                                                   @Valid @RequestBody(required = false) DecisionEditorialeDTO decision) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.demanderModification(id, commentaire(decision))));
    }

    @PreAuthorize("hasAuthority('ARTICLE_VALIDER')")
    @PatchMapping("/{id}/refuser")
    public ResponseEntity<ArticleResponseDTO> refuserArticle(@PathVariable Long id,
                                                             @Valid @RequestBody(required = false) DecisionEditorialeDTO decision) {
        return ResponseEntity.ok(articleMapper.toResponseDTO(articleService.refuserArticle(id, commentaire(decision))));
    }

    // ==================== UTILITAIRES ====================

    private List<ArticleResponseDTO> versDTOs(List<Article> articles) {
        return articles.stream().map(articleMapper::toResponseDTO).toList();
    }

    private String commentaire(DecisionEditorialeDTO decision) {
        return decision == null ? null : decision.getCommentaire();
    }
}
