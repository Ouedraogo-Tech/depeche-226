package com.depeche.infos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

// Une image envoyée sur le site (article, photo de profil, logo), stockée DANS la base de données :
// l'hébergeur n'a pas de disque permanent, un dossier "uploads/" serait vidé à chaque redémarrage.
// Elle reste servie à la même adresse : /uploads/{nom}
@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom aléatoire (ex. : "3f2c…e1.jpg"), utilisé dans l'adresse /uploads/{nom}
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    // Ex. : "image/jpeg"
    @Column(nullable = false, length = 50)
    private String typeContenu;

    // Le contenu de l'image (colonne "bytea" dans PostgreSQL)
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(nullable = false)
    private byte[] donnees;

    private LocalDateTime dateCreation = LocalDateTime.now();

    public Media(String nom, String typeContenu, byte[] donnees) {
        this.nom = nom;
        this.typeContenu = typeContenu;
        this.donnees = donnees;
    }
}
