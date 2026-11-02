package com.depeche.infos.mapper;

import com.depeche.infos.dto.AProposDTO;
import com.depeche.infos.dto.ParametreSiteDTO;
import com.depeche.infos.entity.Engagement;
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
        dto.setLienFacebook(parametre.getLienFacebook());
        dto.setLienYoutube(parametre.getLienYoutube());
        dto.setLienWhatsapp(parametre.getLienWhatsapp());
        dto.setLienInstagram(parametre.getLienInstagram());
        dto.setLienLinkedin(parametre.getLienLinkedin());
        dto.setMentionsLegales(parametre.getMentionsLegales());
        dto.setPolitiqueConfidentialite(parametre.getPolitiqueConfidentialite());
        dto.setLogo(parametre.getLogo());
        dto.setLogoBlanc(parametre.getLogoBlanc());
        dto.setLogoComplet(parametre.getLogoComplet());
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
        parametre.setLienFacebook(vide(dto.getLienFacebook()));
        parametre.setLienYoutube(vide(dto.getLienYoutube()));
        parametre.setLienWhatsapp(vide(dto.getLienWhatsapp()));
        parametre.setLienInstagram(vide(dto.getLienInstagram()));
        parametre.setLienLinkedin(vide(dto.getLienLinkedin()));
        parametre.setMentionsLegales(vide(dto.getMentionsLegales()));
        parametre.setPolitiqueConfidentialite(vide(dto.getPolitiqueConfidentialite()));
        parametre.setLogo(vide(dto.getLogo()));
        parametre.setLogoBlanc(vide(dto.getLogoBlanc()));
        parametre.setLogoComplet(vide(dto.getLogoComplet()));
    }

    // Page "À propos"
    public AProposDTO toAProposDTO(ParametreSite parametre) {
        if (parametre == null) return null;
        AProposDTO dto = new AProposDTO();
        dto.setTexte(parametre.getAproposTexte());
        dto.setAnneeCreation(parametre.getAnneeCreation());
        dto.setEngagements(parametre.getEngagements().stream().map(e -> {
            AProposDTO.EngagementDTO engagement = new AProposDTO.EngagementDTO();
            engagement.setTitre(e.getTitre());
            engagement.setTexte(e.getTexte());
            return engagement;
        }).toList());
        dto.setDateMiseAJour(parametre.getDateMiseAJour());
        return dto;
    }

    // Copie la page "À propos" dans l'entité (la liste est vidée puis remplie : Hibernate garde la même collection)
    public void copierAProposDans(AProposDTO dto, ParametreSite parametre) {
        parametre.setAproposTexte(dto.getTexte().trim());
        parametre.setAnneeCreation(dto.getAnneeCreation());
        parametre.getEngagements().clear();
        dto.getEngagements().forEach(e -> parametre.getEngagements().add(new Engagement(e.getTitre().trim(), e.getTexte().trim())));
    }

    // "" ou espaces → null (le footer cache simplement le logo / le lien)
    private static String vide(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }
}
