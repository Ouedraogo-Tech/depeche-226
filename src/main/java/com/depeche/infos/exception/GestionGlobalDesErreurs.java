package com.depeche.infos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GestionGlobalDesErreurs {

    // Gère les erreurs quand on ne trouve pas une ressource (ex: Article introuvable)
    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(RessourceIntrouvableException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("erreur", "Ressource introuvable");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Gère les autres erreurs classiques (ex: Email déjà utilisé)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("erreur", "Erreur de traitement");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    // Gère les erreurs de validation (@NotBlank, @NotNull...)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();

        // On récupère le message d'erreur exact (ex: "Le titre est obligatoire")
        String messageErreur = ex.getBindingResult().getFieldError().getDefaultMessage();
        response.put("erreur", "Données invalides");
        response.put("message", messageErreur);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}