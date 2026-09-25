package com.depeche.infos.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Petit utilitaire pour récupérer l'email de l'utilisateur connecté (extrait du token JWT par le filtre).
// Évite de répéter les 3 lignes SecurityContextHolder... dans chaque contrôleur.
public final class UtilisateurConnecte {

    private UtilisateurConnecte() {
    }

    public static String email() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
