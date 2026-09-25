package com.depeche.infos.service;


import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {


    private final CategorieRepository categorieRepository;
    private final ArticleRepository articleRepository;

    public CategorieService(CategorieRepository categorieRepository, ArticleRepository articleRepository) {
        this.categorieRepository = categorieRepository;
        this.articleRepository = articleRepository;
    }


    //creer une categorie
    public Categorie creerCategorie(CategorieRequestDTO request) {
        if (categorieRepository.existsByNom(request.getNom())) {
            throw new RuntimeException("Cette catégorie existe déjà !");
        }
        Categorie categorie = new Categorie();
        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());
        return categorieRepository.save(categorie);
    }


    // lister toutes les categorie (triées par nom pour le menu "Actualités")
    public List<Categorie> listerToutesLesCategories() {
        return categorieRepository.findAll(Sort.by("nom"));
    }

    public Categorie trouverCategorieParId(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Catégorie introuvable avec l'id : " + id));
    }

    // modifier une categorie par id
    public Categorie modifierCategorie(Long id, CategorieRequestDTO request) {
        Categorie categorieExistante = trouverCategorieParId(id);

        // Correction : empêcher de renommer une catégorie avec le nom d'une autre
        boolean nomChange = !categorieExistante.getNom().equalsIgnoreCase(request.getNom());
        if (nomChange && categorieRepository.existsByNom(request.getNom())) {
            throw new RuntimeException("Cette catégorie existe déjà !");
        }

        categorieExistante.setNom(request.getNom());
        categorieExistante.setDescription(request.getDescription());

        return categorieRepository.save(categorieExistante);
    }

    // supprimer un categorie par id
    // Correction : impossible de supprimer une catégorie encore utilisée par des articles
    // (avant : erreur de clé étrangère peu compréhensible)
    public void supprimerCategorie(Long id) {
        Categorie categorie = trouverCategorieParId(id);
        if (articleRepository.existsByCategorie(categorie)) {
            throw new IllegalStateException("Impossible de supprimer cette catégorie : des articles l'utilisent encore.");
        }
        categorieRepository.delete(categorie);
    }


}
