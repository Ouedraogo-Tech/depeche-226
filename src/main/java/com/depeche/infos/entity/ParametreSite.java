package com.depeche.infos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Paramètres généraux du site, affichés dans le footer et gérés par le Webmaster.
// Il n'existe qu'UNE seule ligne dans cette table (créée au démarrage par DataInitializer).
@Entity
@Table(name = "parametre_site")
@Getter
@Setter
@NoArgsConstructor
public class ParametreSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomSite;

    @Column(length = 500)
    private String slogan;

    private String emailContact;

    private String telephone;

    private String adresse;

    private String ville;

    // Ligne affichée en bas du footer (ex : "Édité à Ouagadougou · ISSN 2226-0226")
    private String mentionEdition;

    private LocalDateTime dateMiseAJour;
}
