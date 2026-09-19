package com.depeche.infos.entity;

import com.depeche.infos.enums.StatutCommentaire;
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
    @Enumerated(EnumType.STRING) // Dit à Hibernate de stocker le mot "PUBLIE"
    @Column(nullable = false)
    private StatutCommentaire statut = StatutCommentaire.PUBLIE;


    // L'auteur du commentaire (Lecteur ou Utilisateur connecté)
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur auteur;

    // L'article concerné
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}