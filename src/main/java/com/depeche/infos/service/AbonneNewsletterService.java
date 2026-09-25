package com.depeche.infos.service;

import com.depeche.infos.entity.AbonneNewsletter;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.AbonneNewsletterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AbonneNewsletterService {

    private final AbonneNewsletterRepository abonneRepository;

    public AbonneNewsletterService(AbonneNewsletterRepository abonneRepository) {
        this.abonneRepository = abonneRepository;
    }

    public AbonneNewsletter abonner(String email) {
        String emailNormalise = email.trim().toLowerCase();
        Optional<AbonneNewsletter> existant = abonneRepository.findByEmail(emailNormalise);

        if (existant.isPresent()) {
            AbonneNewsletter abonne = existant.get();
            if (abonne.isActif()) {
                throw new IllegalStateException("Cet email est déjà abonné à la newsletter.");
            }
            // Correction : un ancien abonné qui se réabonne est simplement réactivé
            // (avant : erreur "déjà abonné" alors qu'il était désabonné)
            abonne.setActif(true);
            abonne.setDateAbonnement(LocalDateTime.now());
            return abonneRepository.save(abonne);
        }

        AbonneNewsletter abonne = new AbonneNewsletter(emailNormalise);
        abonne.setDateAbonnement(LocalDateTime.now());
        abonne.setActif(true);
        return abonneRepository.save(abonne);
    }

    public void desabonner(String email) {
        AbonneNewsletter abonne = abonneRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RessourceIntrouvableException("Aucun abonné trouvé avec cet email."));
        abonne.setActif(false); // Soft delete : on désactive au lieu de supprimer
        abonneRepository.save(abonne);
    }

    public List<AbonneNewsletter> listerAbonnesActifs() {
        return abonneRepository.findByActif(true);
    }

    // Webmaster : tous les abonnés (actifs et désabonnés), les plus récents en premier
    public List<AbonneNewsletter> listerTousLesAbonnes() {
        return abonneRepository.findAllByOrderByDateAbonnementDesc();
    }
}
