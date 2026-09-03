package com.depeche.infos.service;

import com.depeche.infos.entity.ResponsableEditorial;
import com.depeche.infos.repository.ResponsableEditorialRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class ResponsableEditorialService {

    private final ResponsableEditorialRepository responsableEditorialRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ResponsableEditorialService(ResponsableEditorialRepository responsableEditorialRepository, UtilisateurRepository utilisateurRepository) {
        this.responsableEditorialRepository = responsableEditorialRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public ResponsableEditorial creerResponsableEditorial(ResponsableEditorial responsableEditorial) {
        if (utilisateurRepository.findByEmail(responsableEditorial.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }



        return responsableEditorialRepository.save(responsableEditorial);
    }
}