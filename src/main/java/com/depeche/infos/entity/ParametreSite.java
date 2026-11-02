package com.depeche.infos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // Réseaux sociaux : liens affichés sous forme de logos dans le footer (vide = logo caché)
    private String lienFacebook;
    private String lienYoutube;
    private String lienWhatsapp;
    private String lienInstagram;
    private String lienLinkedin;

    // Pages légales : textes saisis par le webmaster, affichés sur /mentions-legales et /confidentialite
    @Column(columnDefinition = "TEXT")
    private String mentionsLegales;

    @Column(columnDefinition = "TEXT")
    private String politiqueConfidentialite;

    // Logos envoyés par le webmaster (adresse "/uploads/…") ; vide = le logo d'origine du site
    private String logo;         // fond clair : en-tête du site public
    private String logoBlanc;    // fond sombre : barre des espaces de travail
    private String logoComplet;  // avec le slogan : pages de connexion

    // Page "À propos" (gérée par le webmaster) : texte de présentation, année de création, engagements
    @Column(columnDefinition = "TEXT")
    private String aproposTexte;

    private Integer anneeCreation;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "parametre_site_engagement", joinColumns = @JoinColumn(name = "parametre_site_id"))
    @OrderColumn(name = "ordre")
    private List<Engagement> engagements = new ArrayList<>();

    private LocalDateTime dateMiseAJour;
}
