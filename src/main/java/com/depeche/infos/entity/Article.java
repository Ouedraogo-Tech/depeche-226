package com.depeche.infos.entity;

import com.depeche.infos.enums.StatutArticle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    private String image;

    private LocalDateTime dateCreation;

    private LocalDateTime datePublication;

    @Enumerated(EnumType.STRING) // Dit à Hibernate de stocker le mot "BROUILLON" dans la base
    @Column(nullable = false)
    private StatutArticle statut;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

}