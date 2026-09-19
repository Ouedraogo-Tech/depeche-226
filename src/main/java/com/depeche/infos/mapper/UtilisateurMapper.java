package com.depeche.infos.mapper;


import com.depeche.infos.dto.UtilisateurRequestDTO;
import com.depeche.infos.dto.UtilisateurResponseDTO;
import com.depeche.infos.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    public Utilisateur toEntity(UtilisateurRequestDTO request) {
        if (request == null) return null;

        Utilisateur utilisateur;

        switch (request.getTypeUtilisateur()) {
            case ROLE_ADMIN:
                utilisateur = new Administrateur();
                break;
            case ROLE_WEBMASTER:
                utilisateur = new Webmaster();
                break;
            case ROLE_RESPONSABLE_EDITORIAL:
                utilisateur = new ResponsableEditorial();
                break;
            case ROLE_JOURNALISTE:
                utilisateur = new Journaliste();
                break;
            default:
                throw new RuntimeException("Type d'utilisateur inconnu");
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(request.getMotDePasse());

        // ON A SUPPRIMÉ LA PARTIE DES ROLES ICI

        return utilisateur;
    }

    public UtilisateurResponseDTO toResponseDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        UtilisateurResponseDTO dto = new UtilisateurResponseDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        return dto;
    }

}