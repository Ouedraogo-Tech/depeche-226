package com.depeche.infos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "abonne_newsletter")
@Getter
@Setter
@NoArgsConstructor
public class AbonneNewsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime dateAbonnement = LocalDateTime.now();

    @Column(nullable = false)
    private boolean actif = true;

    public AbonneNewsletter(String email) {
        this.email = email;
    }
}