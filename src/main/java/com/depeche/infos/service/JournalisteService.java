package com.depeche.infos.service;

import com.depeche.infos.entity.Journaliste;
import com.depeche.infos.repository.JournalisteRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class JournalisteService {

    private final JournalisteRepository journalisteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public JournalisteService(JournalisteRepository journalisteRepository, UtilisateurRepository utilisateurRepository) {
        this.journalisteRepository = journalisteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Journaliste creerJournaliste(Journaliste journaliste) {
        if (utilisateurRepository.findByEmail(journaliste.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }



        return journalisteRepository.save(journaliste);
    }
}