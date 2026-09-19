package com.depeche.infos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequestDTO {

    // @NotBlank : Le titre ne peut pas être null ni vide (pas d'espaces uniquement)
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    // @NotBlank : Le contenu est obligatoire
    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    // L'image n'est pas obligatoire, donc pas d'annotation ici
    private String image;

    // @NotNull : L'ID de la catégorie doit être fourni (il ne peut pas être null)
    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;
}