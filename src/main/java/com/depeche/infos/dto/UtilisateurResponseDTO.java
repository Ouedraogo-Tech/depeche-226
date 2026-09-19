package com.depeche.infos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilisateurResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
}
