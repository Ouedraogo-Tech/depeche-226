package com.depeche.infos.mapper;

import com.depeche.infos.dto.ParametreSiteDTO;
import com.depeche.infos.entity.ParametreSite;
import org.springframework.stereotype.Component;

@Component
public class ParametreSiteMapper {

    public ParametreSiteDTO toDTO(ParametreSite parametre) {
        if (parametre == null) return null;
        ParametreSiteDTO dto = new ParametreSiteDTO();
        dto.setNomSite(parametre.getNomSite());
        dto.setSlogan(parametre.getSlogan());
        dto.setEmailContact(parametre.getEmailContact());
        dto.setTelephone(parametre.getTelephone());
        dto.setAdresse(parametre.getAdresse());
        dto.setVille(parametre.getVille());
        dto.setMentionEdition(parametre.getMentionEdition());
        dto.setDateMiseAJour(parametre.getDateMiseAJour());
        return dto;
    }

    // Copie les valeurs du DTO dans l'entité existante (on ne crée jamais une 2e ligne)
    public void copierDans(ParametreSiteDTO dto, ParametreSite parametre) {
        parametre.setNomSite(dto.getNomSite());
        parametre.setSlogan(dto.getSlogan());
        parametre.setEmailContact(dto.getEmailContact());
        parametre.setTelephone(dto.getTelephone());
        parametre.setAdresse(dto.getAdresse());
        parametre.setVille(dto.getVille());
        parametre.setMentionEdition(dto.getMentionEdition());
    }
}
