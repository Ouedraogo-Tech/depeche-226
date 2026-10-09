package com.depeche.infos.enums;

// Type de notification : permet au frontend de choisir la couleur / l'icône à afficher.
public enum TypeNotification {
    ARTICLE_SOUMIS,       // -> Responsable éditorial : un article attend sa validation
    ARTICLE_VALIDE,       // -> Journaliste : son article a été validé et publié
    ARTICLE_A_REVISER,    // -> Journaliste : le responsable demande des modifications
    ARTICLE_REFUSE,       // -> Journaliste : son article a été refusé
    ARTICLE_PUBLIE,       // -> Journaliste : son article planifié vient d'être publié
    NOUVEAU_COMMENTAIRE,  // -> Journaliste : un lecteur a commenté son article
    NOUVELLE_CATEGORIE    // -> Journalistes : le responsable éditorial a créé une nouvelle catégorie
}
