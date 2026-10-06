package com.depeche.infos.entity;

import com.depeche.infos.enums.StatutArticle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    // Chapeau affiché sur la page d'accueil (facultatif)
    @Column(length = 1000)
    private String resume;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    private String image;

    // Lien de la vidéo YouTube qui accompagne l'article (facultatif) : la vidéo reste sur YouTube
    private String lienVideo;

    private LocalDateTime dateCreation;

    private LocalDateTime datePublication;

    // Date de mise en ligne demandée par le journaliste en soumettant l'article (null = dès la validation).
    // Appliquée seulement si le responsable éditorial valide ; effacée en cas de refus ou de révision.
    private LocalDateTime datePublicationSouhaitee;

    @Enumerated(EnumType.STRING) // Dit à Hibernate de stocker le mot "BROUILLON" dans la base
    @Column(nullable = false, length = 20)
    private StatutArticle statut;

    // Dernier retour du responsable éditorial (validation, demande de modification ou refus)
    @Column(columnDefinition = "TEXT")
    private String commentaireEditorial;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;
}
