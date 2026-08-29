package com.depeche.infos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "journaliste")
@Getter
@Setter
public class Journaliste extends Utilisateur {

    @Column(nullable = true)
    private String specialite;

}