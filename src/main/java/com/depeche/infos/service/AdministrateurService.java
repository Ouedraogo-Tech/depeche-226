package com.depeche.infos.service;

import com.depeche.infos.entity.Administrateur;
import com.depeche.infos.repository.AdministrateurRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class AdministrateurService {

    private final AdministrateurRepository administrateurRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AdministrateurService(AdministrateurRepository administrateurRepository, UtilisateurRepository utilisateurRepository) {
        this.administrateurRepository = administrateurRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Administrateur creerAdministrateur(Administrateur administrateur) {
        if (utilisateurRepository.findByEmail(administrateur.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }



        return administrateurRepository.save(administrateur);
    }
}