package com.depeche.infos.service;


import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {


    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
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


    // lister toutes les categorie
    public List<Categorie> listerToutesLesCategories() {
        return categorieRepository.findAll();
    }

    public Categorie trouverCategorieParId(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Catégorie introuvable avec l'id : " + id));
    }

    // modifier une categorie par id
    public Categorie modifierCategorie(Long id, CategorieRequestDTO request) {
        Categorie categorieExistante = categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Catégorie introuvable avec l'id : " + id));

        categorieExistante.setNom(request.getNom());
        categorieExistante.setDescription(request.getDescription());

        return categorieRepository.save(categorieExistante);
    }

    // supprimer un categorie par id
    public void supprimerCategorie(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Catégorie introuvable avec l'id : " + id);
        }
        categorieRepository.deleteById(id);
    }


}
