package com.depeche.infos.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

// Toutes les erreurs renvoient le même format JSON : { "erreur": "...", "message": "..." }
// Spring choisit TOUJOURS le handler le plus précis (ex : AccessDeniedException avant RuntimeException).
@RestControllerAdvice
public class GestionGlobalDesException {

    // Petite méthode utilitaire pour éviter de répéter la construction de la réponse
    private ResponseEntity<Map<String, String>> reponse(HttpStatus status, String erreur, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("erreur", erreur);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    // 1. Gère les erreurs 404 (Ressource introuvable)
    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(RessourceIntrouvableException ex) {
        return reponse(HttpStatus.NOT_FOUND, "Ressource introuvable", ex.getMessage());
    }

    // 1bis. URL inexistante -> 404 (au lieu d'une erreur 500)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResource(NoResourceFoundException ex) {
        return reponse(HttpStatus.NOT_FOUND, "Ressource introuvable", "Cette adresse n'existe pas.");
    }

    // 2. Gère les erreurs de connexion (Mauvais mot de passe) -> Code 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return reponse(HttpStatus.UNAUTHORIZED, "Non autorisé", "Les identifications sont erronées");
    }

    // 2bis. Connexion d'un compte suspendu -> 403
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabled(DisabledException ex) {
        return reponse(HttpStatus.FORBIDDEN, "Compte suspendu",
                "Votre compte est suspendu. Contactez l'administrateur.");
    }

    // 2ter. Correction : un refus de @PreAuthorize renvoyait 400 (attrapé par le handler RuntimeException).
    // Il renvoie maintenant 403 comme attendu.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return reponse(HttpStatus.FORBIDDEN, "Accès refusé", "Vous n'avez pas les droits pour effectuer cette action.");
    }

    // 2quater. Action interdite sur une ressource précise (ex : l'article d'un collègue) -> 403
    @ExceptionHandler(ActionInterditeException.class)
    public ResponseEntity<Map<String, String>> handleActionInterdite(ActionInterditeException ex) {
        return reponse(HttpStatus.FORBIDDEN, "Accès refusé", ex.getMessage());
    }

    // 3. Gère les erreurs de validation (@NotBlank, @NotNull...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        String messageErreur = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Les données envoyées sont invalides";
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", messageErreur);
    }

    // 3bis. JSON mal formé ou valeur d'enum inconnue (ex : "typeUtilisateur": "ROLE_INCONNU")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException ex) {
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", "Le corps de la requête est invalide ou mal formé.");
    }

    // 3ter. Paramètre d'URL manquant ou du mauvais type (ex : ?articleId=abc)
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            DateTimeParseException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleParametreInvalide(Exception ex) {
        String message = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Paramètre manquant ou invalide.";
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", message);
    }

    // 4. Opération impossible dans l'état actuel (ex : valider un article qui n'est pas "SOUMIS") -> 409
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return reponse(HttpStatus.CONFLICT, "Opération impossible", ex.getMessage());
    }

    // 4bis. Contrainte de base de données violée (clé étrangère, valeur unique...) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return reponse(HttpStatus.CONFLICT, "Opération impossible",
                "Cette opération entre en conflit avec des données existantes.");
    }

    // 4ter. Fichier envoyé trop volumineux -> 413
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUpload(MaxUploadSizeExceededException ex) {
        return reponse(HttpStatus.PAYLOAD_TOO_LARGE, "Fichier trop volumineux", "La taille maximale autorisée est de 5 Mo.");
    }

    // 5. Le filet de sécurité (Toutes les autres erreurs métier, ex: Email déjà utilisé)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return reponse(HttpStatus.BAD_REQUEST, "Erreur de traitement", ex.getMessage());
    }

    // 6. Erreur imprévue (non RuntimeException) -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne", "Une erreur inattendue est survenue.");
    }
}
