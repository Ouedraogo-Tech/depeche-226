package com.depeche.infos.service;

import com.depeche.infos.entity.Lecteur;
import com.depeche.infos.repository.LecteurRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class LecteurService {

    private final LecteurRepository lecteurRepository;
    private final UtilisateurRepository utilisateurRepository;

    public LecteurService(LecteurRepository lecteurRepository, UtilisateurRepository utilisateurRepository) {
        this.lecteurRepository = lecteurRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Lecteur creerLecteur(Lecteur lecteur) {
        if (utilisateurRepository.findByEmail(lecteur.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }



        return lecteurRepository.save(lecteur);
    }
}