package com.depeche.infos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "commentaire")
@Getter
@Setter
@NoArgsConstructor
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    private LocalDateTime dateCreation = LocalDateTime.now();

    // Statut par défaut : "PUBLIE". Peut passer à "MASQUE" ou "SUPPRIME" par le webmaster.
    @Column(nullable = false)
    private String statut = "PUBLIE";


    // L'auteur du commentaire (Lecteur ou Utilisateur connecté)
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur auteur;

    // L'article concerné
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}