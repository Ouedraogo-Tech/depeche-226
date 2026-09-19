package com.depeche.infos.exception;

// On étend RuntimeException car c'est une erreur qui arrive à l'exécution
public class RessourceIntrouvableException extends RuntimeException {

    // Constructeur qui prend un message en paramètre
    public RessourceIntrouvableException(String message) {
        // On transmet le message à la classe mère (RuntimeException).
        super(message);
    }
}