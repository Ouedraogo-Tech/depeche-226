package com.depeche.infos.mapper;


import com.depeche.infos.dto.UtilisateurRequestDTO;
import com.depeche.infos.dto.UtilisateurResponseDTO;
import com.depeche.infos.entity.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setMotDePasse(request.getMotDePasse());



        return utilisateur;
    }

    public UtilisateurResponseDTO toResponseDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        UtilisateurResponseDTO dto = new UtilisateurResponseDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());

        // On transforme les objets Rôle en simple texte (ex: "ADMIN")
        Set<String> roleNames = utilisateur.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);
        return dto;
    }

}