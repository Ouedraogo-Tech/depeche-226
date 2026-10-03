package com.depeche.infos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

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

    // Code secret du lien "Se désabonner" des e-mails (jamais renvoyé par l'API : @JsonIgnore)
    @JsonIgnore
    @Column(unique = true, length = 36)
    private String jeton = UUID.randomUUID().toString();

    public AbonneNewsletter(String email) {
        this.email = email;
    }
}
