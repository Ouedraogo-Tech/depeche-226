package com.depeche.infos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Un engagement de la rédaction, affiché sur la page "À propos" (ex. : "Vérifier avant de publier")
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Engagement {

    @Column(nullable = false, length = 100)
    private String titre;

    @Column(nullable = false, length = 500)
    private String texte;
}
