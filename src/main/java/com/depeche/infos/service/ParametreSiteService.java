package com.depeche.infos.service;

import com.depeche.infos.dto.ParametreSiteDTO;
import com.depeche.infos.entity.ParametreSite;
import com.depeche.infos.mapper.ParametreSiteMapper;
import com.depeche.infos.repository.ParametreSiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ParametreSiteService {

    private final ParametreSiteRepository parametreSiteRepository;
    private final ParametreSiteMapper parametreSiteMapper;

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

    // Valeurs reprises de la maquette (footer de la page d'accueil)
    private ParametreSite parametresParDefaut() {
        ParametreSite parametre = new ParametreSite();
        parametre.setNomSite("Dépêche 226");
        parametre.setSlogan("Le Burkina Faso raconté par ses journalistes. Information vérifiée, terrain d'abord.");
        parametre.setEmailContact("redaction@depeche226.bf");
        parametre.setTelephone("+226 25 30 12 26");
        parametre.setAdresse("Avenue Kwame N'Krumah");
        parametre.setVille("Ouagadougou, Burkina Faso");
        parametre.setMentionEdition("Édité à Ouagadougou · ISSN 2226-0226");
        parametre.setDateMiseAJour(LocalDateTime.now());
        return parametre;
    }
}
