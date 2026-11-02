package com.depeche.infos.service;

import com.depeche.infos.dto.AProposDTO;
import com.depeche.infos.dto.ParametreSiteDTO;
import com.depeche.infos.entity.Engagement;
import com.depeche.infos.entity.ParametreSite;
import com.depeche.infos.mapper.ParametreSiteMapper;
import com.depeche.infos.repository.ParametreSiteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParametreSiteService {

    private final ParametreSiteRepository parametreSiteRepository;
    private final ParametreSiteMapper parametreSiteMapper;

    // Valeurs de DÉPART du footer, lues dans application.properties (fichier non envoyé sur GitHub).
    // Elles ne servent qu'une fois : quand la table parametre_site est encore vide.
    // Ensuite, c'est le webmaster qui modifie tout depuis "Paramètres du site".
    @Value("${app.site.nom:}")
    private String nomSite;
    @Value("${app.site.slogan:}")
    private String slogan;
    @Value("${app.site.email-contact:}")
    private String emailContact;
    @Value("${app.site.telephone:}")
    private String telephone;
    @Value("${app.site.adresse:}")
    private String adresse;
    @Value("${app.site.ville:}")
    private String ville;
    @Value("${app.site.mention-edition:}")
    private String mentionEdition;

    public ParametreSiteService(ParametreSiteRepository parametreSiteRepository, ParametreSiteMapper parametreSiteMapper) {
        this.parametreSiteRepository = parametreSiteRepository;
        this.parametreSiteMapper = parametreSiteMapper;
    }

    // 1. Lire les paramètres (on crée les valeurs par défaut s'ils n'existent pas encore)
    public ParametreSite obtenirParametres() {
        return parametreSiteRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> parametreSiteRepository.save(parametresParDefaut()));
    }

    // 2. Modifier les paramètres (Webmaster)
    public ParametreSite modifierParametres(ParametreSiteDTO dto) {
        ParametreSite parametre = obtenirParametres();
        parametreSiteMapper.copierDans(dto, parametre);
        parametre.setDateMiseAJour(LocalDateTime.now());
        return parametreSiteRepository.save(parametre);
    }

    // 3. Modifier la page "À propos" (Webmaster) : n'écrase aucun autre paramètre
    public ParametreSite modifierAPropos(AProposDTO dto) {
        ParametreSite parametre = obtenirParametres();
        parametreSiteMapper.copierAProposDans(dto, parametre);
        parametre.setDateMiseAJour(LocalDateTime.now());
        return parametreSiteRepository.save(parametre);
    }

    // Au démarrage : si la page "À propos" n'a jamais été remplie, on y met les textes d'origine du site
    // (le texte de présentation est obligatoire, donc vide = jamais rempli)
    public void initialiserAProposSiVide() {
        ParametreSite parametre = obtenirParametres();
        if (parametre.getAproposTexte() != null && !parametre.getAproposTexte().isBlank()) {
            return;
        }
        parametre.setAproposTexte("""
                Dépêche 226 est un média d'information en ligne consacré au Burkina Faso et au Sahel. Notre rôle : \
                informer vite, mais surtout informer juste, sur la politique, l'économie, la société, la culture et le sport.

                Les articles sont écrits par nos journalistes, puis relus et validés par un responsable éditorial avant \
                d'être publiés. Ce double regard est notre garantie de qualité.""");
        if (parametre.getAnneeCreation() == null) {
            parametre.setAnneeCreation(2019);
        }
        if (parametre.getEngagements().isEmpty()) {
            parametre.getEngagements().addAll(List.of(
                    new Engagement("Vérifier avant de publier", "Chaque article est relu par un responsable éditorial avant sa mise en ligne."),
                    new Engagement("Le terrain d'abord", "Nos journalistes racontent le Burkina Faso depuis les régions, au plus près des faits."),
                    new Engagement("Indépendance", "Nous ne publions pas de contenu sponsorisé déguisé en information."),
                    new Engagement("Ouverture au débat", "Les lecteurs peuvent réagir sous chaque article, dans le respect de chacun.")));
        }
        parametreSiteRepository.save(parametre);
    }

    // Première ligne de la table : valeurs de application.properties (aucune coordonnée écrite dans le code).
    // Seul le nom du site a une valeur de secours, car il est obligatoire.
    private ParametreSite parametresParDefaut() {
        ParametreSite parametre = new ParametreSite();
        parametre.setNomSite(nomSite.isBlank() ? "Dépêche 226" : nomSite.trim());
        parametre.setSlogan(videVersNull(slogan));
        parametre.setEmailContact(videVersNull(emailContact));
        parametre.setTelephone(videVersNull(telephone));
        parametre.setAdresse(videVersNull(adresse));
        parametre.setVille(videVersNull(ville));
        parametre.setMentionEdition(videVersNull(mentionEdition));
        parametre.setDateMiseAJour(LocalDateTime.now());
        return parametre;
    }

    // "" → null : le footer cache simplement la ligne
    private static String videVersNull(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }
}
