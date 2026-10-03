package com.depeche.infos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UtilisateurResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    // Photo de profil (null si aucune : le frontend affiche alors les initiales)
    private String photo;
    private Set<String> roles;
    // Ajout : nécessaire pour afficher "Actif / Désactivé" dans l'espace Administrateur
    private boolean actif;
    private LocalDateTime dateInscription;
}
