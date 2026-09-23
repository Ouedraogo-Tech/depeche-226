package com.depeche.infos.service;

import com.depeche.infos.entity.AbonneNewsletter;
import com.depeche.infos.repository.AbonneNewsletterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AbonneNewsletterService {

    private final AbonneNewsletterRepository abonneRepository;

    public AbonneNewsletterService(AbonneNewsletterRepository abonneRepository) {
        this.abonneRepository = abonneRepository;
    }

    public AbonneNewsletter abonner(String email) {
        if (abonneRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà abonné à la newsletter.");
        }

        AbonneNewsletter abonne = new AbonneNewsletter(email);
        abonne.setDateAbonnement(LocalDateTime.now());
        abonne.setActif(true);

        return abonneRepository.save(abonne);
    }

    public void desabonner(String email) {
        AbonneNewsletter abonne = abonneRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun abonné trouvé avec cet email."));

        abonne.setActif(false); // Soft delete : on désactive au lieu de supprimer
        abonneRepository.save(abonne);
    }

    public List<AbonneNewsletter> listerAbonnesActifs() {
        return abonneRepository.findByActif(true);
    }
}