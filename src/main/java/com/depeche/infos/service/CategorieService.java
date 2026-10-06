package com.depeche.infos.service;


import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class CategorieService {


    private final CategorieRepository categorieRepository;
    private final ArticleRepository articleRepository;

    public CategorieService(CategorieRepository categorieRepository, ArticleRepository articleRepository) {
        this.categorieRepository = categorieRepository;
        this.articleRepository = articleRepository;
    }


    //creer une categorie
    // Doublon refusé même si seules les majuscules, les accents ou les espaces changent
    // ("Politique" = "politique" = " POLITIQUE ", "Économie" = "Economie")
    public Categorie creerCategorie(CategorieRequestDTO request) {
        String nom = request.getNom().trim();
        if (nomDejaPris(nom, null)) {
            throw new IllegalStateException("La catégorie « " + nom + " » existe déjà.");
        }
        Categorie categorie = new Categorie();
        categorie.setNom(nom);
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
        String nom = request.getNom().trim();
        if (nomDejaPris(nom, id)) {
            throw new IllegalStateException("La catégorie « " + nom + " » existe déjà.");
        }

        categorieExistante.setNom(nom);
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

    // Une AUTRE catégorie (id différent de idIgnore) porte-t-elle déjà ce nom ?
    private boolean nomDejaPris(String nom, Long idIgnore) {
        String cle = normaliser(nom);
        return categorieRepository.findAll().stream()
                .anyMatch(c -> !c.getId().equals(idIgnore) && normaliser(c.getNom()).equals(cle));
    }

    // "  Économie " → "economie" : sans accents, en minuscules, espaces multiples réduits
    private static String normaliser(String texte) {
        return Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }


}
