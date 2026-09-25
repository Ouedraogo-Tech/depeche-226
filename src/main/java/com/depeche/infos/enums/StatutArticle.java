package com.depeche.infos.enums;

// Cycle de vie d'un article :
// BROUILLON -> SOUMIS -> (PUBLIE | A_REVISER | REFUSE)
// A_REVISER -> SOUMIS (après correction par le journaliste)
// PLANIFIE  -> PUBLIE (automatiquement à la date prévue)
// PUBLIE    -> ARCHIVE
public enum StatutArticle {
    BROUILLON,
    SOUMIS,
    A_REVISER,
    REFUSE,
    PUBLIE,
    PLANIFIE,
    ARCHIVE
}
