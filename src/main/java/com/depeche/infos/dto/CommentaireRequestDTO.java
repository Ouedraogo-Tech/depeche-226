package com.depeche.infos.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentaireRequestDTO {
    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    private String contenu;
}


