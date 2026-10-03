package com.depeche.infos.service;

import com.depeche.infos.dto.ParametreSiteDTO;
import com.depeche.infos.entity.ParametreSite;
import com.depeche.infos.mapper.ParametreSiteMapper;
import com.depeche.infos.repository.ParametreSiteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
