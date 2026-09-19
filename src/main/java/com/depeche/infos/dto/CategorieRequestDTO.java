package com.depeche.infos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorieRequestDTO {

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String nom;

    private String description;
}