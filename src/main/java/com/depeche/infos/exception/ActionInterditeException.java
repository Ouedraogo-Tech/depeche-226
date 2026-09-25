package com.depeche.infos.exception;

// Levée quand l'utilisateur est connecté ET possède la permission,
// mais n'a pas le droit d'agir sur CETTE ressource précise
// (ex : un journaliste qui veut modifier l'article d'un collègue). -> HTTP 403
public class ActionInterditeException extends RuntimeException {

    public ActionInterditeException(String message) {
        super(message);
    }
}
