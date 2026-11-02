package com.depeche.infos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Page "À propos" : lue par tout le monde (GET), modifiée par le Webmaster (PUT).
@Getter
@Setter
public class AProposDTO {

    // Texte de présentation ("Notre mission") : une ligne vide sépare deux paragraphes
    @NotBlank(message = "Le texte de présentation est obligatoire")
    @Size(max = 10000, message = "Le texte de présentation est trop long (10 000 caractères maximum)")
    private String texte;

    // Facultative : vide = la case "Année de création" est cachée
    @Min(value = 1900, message = "L'année de création doit être comprise entre 1900 et 2100")
    @Max(value = 2100, message = "L'année de création doit être comprise entre 1900 et 2100")
    private Integer anneeCreation;

    @NotNull(message = "La liste des engagements est obligatoire (elle peut être vide)")
    @Size(max = 8, message = "8 engagements maximum")
    private List<@Valid EngagementDTO> engagements = new ArrayList<>();

    // Rempli par le serveur, ignoré en entrée
    private LocalDateTime dateMiseAJour;

    @Getter
    @Setter
    public static class EngagementDTO {

        @NotBlank(message = "Chaque engagement doit avoir un titre")
        @Size(max = 100, message = "Le titre d'un engagement ne doit pas dépasser 100 caractères")
        private String titre;

        @NotBlank(message = "Chaque engagement doit avoir un texte")
        @Size(max = 500, message = "Le texte d'un engagement ne doit pas dépasser 500 caractères")
        private String texte;
    }
}
