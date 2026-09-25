package com.depeche.infos.entity;

import com.depeche.infos.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Notification interne adressée à un utilisateur (ex : "Votre article a été validé").
@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeNotification type;

    @Column(nullable = false)
    private boolean lue = false;

    private LocalDateTime dateCreation = LocalDateTime.now();

    // Simple identifiant (et non une relation) : si l'article est supprimé,
    // la notification reste consultable sans bloquer la suppression.
    private Long articleId;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;
}
