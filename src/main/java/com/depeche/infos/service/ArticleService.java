package com.depeche.infos.service;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;

    public ArticleService(ArticleRepository articleRepository, UtilisateurRepository utilisateurRepository, CategorieRepository categorieRepository) {
        this.articleRepository = articleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.categorieRepository = categorieRepository;
    }

    public Article creeArticle(Article article, Long auteurId, Long categorieId) {
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new RessourceIntrouvableException("Auteur introuvable avec l'id: " + auteurId));

        Categorie categorie = categorieRepository.findById(categorieId)
                        .orElseThrow(() -> new RessourceIntrouvableException("Categorie introuvable avec l'id " + categorieId));

        article.setAuteur(auteur);
        article.setStatut(StatutArticle.BROUILLON);
        article.setDateCreation(LocalDateTime.now());
        article.setCategorie(categorie);
        return articleRepository.save(article);
    }

    public Optional<Article> rechercherArticleParId(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> listerTousLesArticles() {
        return articleRepository.findAll();
    }

    public Article modifierArticle(Long id, Article articleModifie) {
        Article articleExistant = articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id " + id));

        articleExistant.setTitre(articleModifie.getTitre());
        articleExistant.setContenu(articleModifie.getContenu());
        articleExistant.setCategorie(articleModifie.getCategorie());
        articleExistant.setImage(articleModifie.getImage());

        return articleRepository.save(articleExistant);
    }

    public void supprimerArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new RessourceIntrouvableException("l'article a supprimer n'existe pas " + id); // <-- Changé ici
        }
        articleRepository.deleteById(id);
    }

    public Article mettreEnBrouillons(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("l'article n'existe pas " + id)); // <-- Changé ici
        article.setStatut(StatutArticle.BROUILLON);
        return articleRepository.save(article);
    }

    public Article soumettreArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("l'article n'existe pas " + id)); // <-- Changé ici
        article.setStatut(StatutArticle.SOUMIS);
        return articleRepository.save(article);
    }

    public Article publierArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("l'article n'existe pas " + id)); // <-- Changé ici
        article.setStatut(StatutArticle.PUBLIE);
        article.setDatePublication(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Article planifierArticle(Long id, LocalDateTime datePublication) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id " + id)); // <-- Changé ici
        article.setStatut(StatutArticle.PLANIFIE);
        article.setDatePublication(datePublication);
        return articleRepository.save(article);
    }

    public Article archiverArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Article introuvable avec l'id " + id)); // <-- Changé ici
        article.setStatut(StatutArticle.ARCHIVE);
        return articleRepository.save(article);
    }

        // 8. Lister les articles d'un auteur précis (pour "Mes articles" côté Journaliste)
    public List<Article> listerArticlesParAuteur(String emailAuteur) {
        Utilisateur auteur = utilisateurRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'email : " + emailAuteur));
        return articleRepository.findByAuteur(auteur);
    }
}