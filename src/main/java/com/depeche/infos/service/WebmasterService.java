package com.depeche.infos.service;

import com.depeche.infos.entity.Webmaster;
import com.depeche.infos.entity.RoleType;
import com.depeche.infos.repository.WebmasterRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class WebmasterService {

    private final WebmasterRepository webmasterRepository;
    private final UtilisateurRepository utilisateurRepository;

    public WebmasterService(WebmasterRepository webmasterRepository, UtilisateurRepository utilisateurRepository) {
        this.webmasterRepository = webmasterRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Webmaster creerWebmaster(Webmaster webmaster) {
        if (utilisateurRepository.findByEmail(webmaster.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        webmaster.setRole(RoleType.WEBMASTER);
        webmaster.setActif(true);

        return webmasterRepository.save(webmaster);
    }
}