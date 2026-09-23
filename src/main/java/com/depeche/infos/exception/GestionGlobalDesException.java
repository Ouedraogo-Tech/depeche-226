package com.depeche.infos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GestionGlobalDesException {

    // 1. Gère les erreurs 404 (Ressource introuvable)
    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(RessourceIntrouvableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erreur", "Ressource introuvable");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 2. Gère les erreurs de connexion (Mauvais mot de passe) -> Code 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erreur", "Non autorisé");
        response.put("message", "Les identifications sont erronées");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 3. Gère les erreurs de validation (@NotBlank, @NotNull...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        String messageErreur = ex.getBindingResult().getFieldError().getDefaultMessage();
        response.put("erreur", "Données invalides");
        response.put("message", messageErreur);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. Le filet de sécurité (Toutes les autres erreurs, ex: Email déjà utilisé)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erreur", "Erreur de traitement");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}