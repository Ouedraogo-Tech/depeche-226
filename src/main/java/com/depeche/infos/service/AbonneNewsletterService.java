package com.depeche.infos.service;

import com.depeche.infos.dto.EnvoiNewsletterDTO;
import com.depeche.infos.dto.ResultatEnvoiDTO;
import com.depeche.infos.entity.AbonneNewsletter;
import com.depeche.infos.entity.Article;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.AbonneNewsletterRepository;
import com.depeche.infos.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AbonneNewsletterService {

    private static final Logger log = LoggerFactory.getLogger(AbonneNewsletterService.class);

    private final AbonneNewsletterRepository abonneRepository;
    private final ArticleRepository articleRepository;
    private final EmailService emailService;

    public AbonneNewsletterService(AbonneNewsletterRepository abonneRepository, ArticleRepository articleRepository,
                                   EmailService emailService) {
        this.abonneRepository = abonneRepository;
        this.articleRepository = articleRepository;
        this.emailService = emailService;
    }

    // Webmaster : envoie la newsletter (les articles choisis) aux abonnés choisis, UN e-mail par abonné.
    // Seuls les abonnés ACTIFS et les articles PUBLIÉS sont pris en compte.
    // Une adresse en erreur ne bloque pas les autres : elle est comptée dans "echecs".
    public ResultatEnvoiDTO envoyerNewsletter(EnvoiNewsletterDTO demande) {
        List<Article> articles = articleRepository.findAllById(demande.getArticleIds()).stream()
                .filter(a -> a.getStatut() == StatutArticle.PUBLIE)
                .sorted(Comparator.comparing(Article::getDatePublication, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        if (articles.isEmpty()) {
            throw new IllegalArgumentException("Aucun des articles choisis n'est publié.");
        }

        List<AbonneNewsletter> abonnes = abonneRepository.findAllById(demande.getAbonneIds()).stream()
                .filter(AbonneNewsletter::isActif)
                .toList();
        if (abonnes.isEmpty()) {
            throw new IllegalArgumentException("Aucun des abonnés choisis n'est actif.");
        }

        String objet = demande.getObjet().trim();
        int envoyes = 0;
        List<String> enEchec = new ArrayList<>();
        for (AbonneNewsletter abonne : abonnes) {
            if (abonne.getJeton() == null) { // sécurité : le lien de désabonnement doit exister
                abonne.setJeton(UUID.randomUUID().toString());
                abonneRepository.save(abonne);
            }
            try {
                emailService.envoyerNewsletter(abonne, objet, articles);
                envoyes++;
            } catch (Exception e) {
                log.warn("Newsletter non envoyée à {} : {}", abonne.getEmail(), e.getMessage());
                enEchec.add(abonne.getEmail());
            }
        }
        return new ResultatEnvoiDTO(envoyes, enEchec.size(), enEchec);
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

    // (Supprimé : desabonner(email) — désabonner par l'adresse permettait de désabonner n'importe qui.)
    // Désabonnement = soft delete : on désactive l'abonné au lieu de le supprimer.

    // Lien "Se désabonner" d'un e-mail : par le JETON secret (et non plus par l'adresse)
    public AbonneNewsletter desabonnerParJeton(String jeton) {
        AbonneNewsletter abonne = abonneRepository.findByJeton(jeton == null ? "" : jeton.trim())
                .orElseThrow(() -> new RessourceIntrouvableException("Ce lien de désabonnement n'est pas valide."));
        abonne.setActif(false);
        return abonneRepository.save(abonne);
    }

    // Bouton "Désabonner" du webmaster (par l'id)
    public AbonneNewsletter desabonnerParId(Long id) {
        AbonneNewsletter abonne = abonneRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Abonné introuvable."));
        abonne.setActif(false);
        return abonneRepository.save(abonne);
    }

    // Au démarrage : les abonnés créés avant l'ajout du jeton en reçoivent un
    public void attribuerJetonsManquants() {
        for (AbonneNewsletter abonne : abonneRepository.findByJetonIsNull()) {
            abonne.setJeton(UUID.randomUUID().toString());
            abonneRepository.save(abonne);
        }
    }

    public List<AbonneNewsletter> listerAbonnesActifs() {
        return abonneRepository.findByActif(true);
    }

    // Webmaster : tous les abonnés (actifs et désabonnés), les plus récents en premier
    public List<AbonneNewsletter> listerTousLesAbonnes() {
        return abonneRepository.findAllByOrderByDateAbonnementDesc();
    }
}
