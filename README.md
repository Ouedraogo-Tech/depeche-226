DéPêche Infos - Backend API

statut : en developpement
java : minum 17 21 ou 25 ou une version plus stable
 springboot 4.1.1
postgreSQL 18.4


1. Titre & Statut du Projet

Nom du projet : DéPêche Infos Backend
Version : 1.0.0
Statut : Backend fonctionnel (Architecture, Sécurité, Tests) terminé.
L'equipe de développement : RUFAI Saratou , PAMOUSSO HENRY JUNIOR, OUEDRAOGO Paul, NITIEMA Asael


2. Description & Contexte

Ce projet est l'API REST (Backend) d'une plateforme média d'actualités. Il a été conçu pour gérer le cycle de vie complet de l'information, de la rédaction à la publication, tout en intégrant une sécurité de niveau entreprise.

Contexte métier :Conçu à l'origine pour un journaliste indépendant travaillant en solo (gestion directe de la publication), l'architecture a été pensée pour être évolutive. Elle permet aujourd'hui de gérer une rédaction complète (Admin, Journalistes, Responsable Éditorial, Webmasters, Lecteurs) grâce à un système de droits RBAC (Role-Based Access Control) dynamique.

Fonctionnalités principales :

    Authentification sécurisée par Token JWT (Stateless).
    Gestion dynamique des rôles et permissions stockée en base de données.
    Workflow éditorial complet (Brouillon ➔ Soumis ➔ Publié ➔ Archivé).
    Système de commentaires avec modération 
    Gestion de la newsletter avec désinscription progressive.
    Documentation API interactive (Swagger UI).

3. Prérequis & Installation
Prérequis techniques

Avant de commencer, assurez-vous d'avoir installé sur votre machine :

    Java JDK 25 (ou 21,17)
    Maven (ou utiliser le wrapper mvnw inclus)
    PostgreSQL (Serveur de base de données)
    Un IDE Java (IntelliJ IDEA recommandé) ou vscode

Étapes d'installation

    Cloner le dépôt

    git clone https://github.com/pamoussop-art/depeche-226.gitcd depeche-226

    Configuration de la base de données
    Par sécurité, le fichier de configuration local est ignoré par Git (.gitignore).
         Renommez le fichier src/main/resources/application-example.properties en application.properties.
         Ouvrez-le et modifiez les variables avec vos identifiants PostgreSQL et générez une clé secrète JWT. avec la commande openssl

    Création de la base de données
    Assurez-vous que votre serveur PostgreSQL est en cours d'exécution, puis créez une base de données vide (ex: depeche_infos). Hibernate créera les tables automatiquement au démarrage.

    Lancement de l'application
    Via l'IDE : Cliquez sur le bouton "Run" de la classe principale InfosApplication.java.
    Ou via le terminal :
    bash

    ./mvnw spring-boot:run
     
     

    Au démarrage, un DataInitializer peuplera automatiquement la base de données avec les rôles et permissions nécessaires.

4. Utilisation & Exemples
Documentation API (Swagger)

Dès que l'application est lancée, la documentation interactive est disponible sur :
http://localhost:8080/swagger-ui.html (selon le port disponible sur votre serveur ) 

Vous pouvez tester toutes les routes directement depuis ce navigateur. Cliquez sur le bouton Authorize pour insérer un Token JWT.
Exemple d'utilisation (Workflow classique)

    Premier compte Admin :
    Créé AUTOMATIQUEMENT au démarrage si aucun ADMIN n'existe en base
    (identifiants : app.admin.email / app.admin.password, à renseigner dans VOTRE
    application.properties — aucun identifiant par défaut dans le code).
    POST /api/utilisateurs est réservé à l'admin (il n'est plus public).

    Se connecter pour obtenir le Token :
    POST /api/auth/login
    json
    { "email": "<app.admin.email>", "motDePasse": "<app.admin.password>" }
     
     

    Réponse : { "token": "eyJhbGciOiJIUzI1NiJ9...", "type": "Bearer " }

    Créer un article (Nécessite le Token d'un Journaliste ou Admin) :
    POST /api/articles
    Ajoutez l'en-tête : Authorization: Bearer <votre_token>
    json
     
      
     
     
    {
      "titre": "Nouveau gouvernement formé",
      "contenu": "Le Président a annoncé...",
      "image": "https://monsite.com/img.jpg",
      "categorieId": 1
    }



Matrice des Droits (RBAC)
Acteur

Droits principaux
Admin	Créer, modifier, suspendre des comptes, attribuer les rôles, statistiques
Journaliste	Rédiger, modifier, soumettre, publier, planifier SES articles
Resp. Éditorial	Valider / demander une modification / refuser les articles soumis, gérer les catégories, statistiques
Webmaster	Modérer les commentaires, gérer la newsletter et les paramètres du footer, statistiques
Lecteur	S'inscrire, commenter les articles publiés


Le token JWT contient : sub (email), id, nom, prenom, roles, permissions, exp.
Toutes les erreurs ont le format : { "erreur": "...", "message": "..." }
(400 données invalides, 401 non connecté / token expiré, 403 droits insuffisants,
404 introuvable, 409 opération impossible dans l'état actuel).

Workflow d'un article :
BROUILLON -> SOUMIS -> PUBLIE | A_REVISER | REFUSE ;  A_REVISER -> SOUMIS ;
PLANIFIE -> PUBLIE (automatique, vérifié chaque minute) ;  PUBLIE -> ARCHIVE


5. Liste des endpoints

PUBLIC (sans token)
    POST   /api/auth/login                        Connexion -> { token }
    POST   /api/auth/inscription                  Inscription d'un LECTEUR
    GET    /api/articles/publies?categorieId=&recherche=   Articles publiés
    GET    /api/articles/publies/{id}             Un article publié
    GET    /api/categories  |  /api/categories/{id}
    GET    /api/commentaires/article/{articleId}  Commentaires publiés d'un article
    GET    /api/parametres                        Paramètres du footer
    POST   /api/newsletter/abonner                { email }
    POST   /api/newsletter/desabonner?email=
    GET    /uploads/{fichier}                     Images des articles

TOUT UTILISATEUR CONNECTÉ
    GET    /api/utilisateurs/moi  |  PUT /api/utilisateurs/moi   Mon profil
    GET    /api/notifications                     Mes notifications
    GET    /api/notifications/non-lues            { nombre }
    PATCH  /api/notifications/{id}/lue  |  PATCH /api/notifications/lire-tout

ARTICLES (back-office)
    GET    /api/articles?statut=SOUMIS            Tous les articles (RE, Admin, Webmaster)
    GET    /api/articles/moi                      Mes articles (Journaliste, RE)
    GET    /api/articles/{id}
    POST   /api/articles                          Créer (statut BROUILLON)
    PUT    /api/articles/{id}  |  DELETE /api/articles/{id}   (auteur ou RE)
    PATCH  /api/articles/{id}/brouillon | soumettre | publier | archiver
    PATCH  /api/articles/{id}/planifier?date=2026-10-01T08:00:00
    PATCH  /api/articles/{id}/valider | demander-modification | refuser   { commentaire } (RE)
    POST   /api/medias  (multipart, champ "fichier")  -> { url }  Image JPG/PNG/WEBP, 5 Mo max

CATÉGORIES (RE)          POST /api/categories, PUT/DELETE /api/categories/{id}
COMMENTAIRES
    POST   /api/commentaires?articleId=           Lecteur
    GET    /api/commentaires/mes-articles         Journaliste
    GET    /api/commentaires?statut=              Webmaster
    PATCH  /api/commentaires/{id}/masquer | publier ,  DELETE /api/commentaires/{id}   Webmaster
NEWSLETTER (Webmaster)   GET /api/newsletter?actifsSeulement=true
PARAMÈTRES (Webmaster)   PUT /api/parametres
STATISTIQUES             GET /api/statistiques      (Admin, RE, Webmaster)
UTILISATEURS (Admin)
    GET/POST /api/utilisateurs ,  PUT /api/utilisateurs/{id}
    PATCH  /api/utilisateurs/{id}/suspendre | activer
    PATCH  /api/utilisateurs/{id}/role?role=JOURNALISTE
    GET    /api/roles                             Rôles et permissions


Standards de code :

     Respect de l'architecture en couches (Entity ➔ Repository ➔ Service ➔ Controller).
     Utilisation systématique des DTOs et Mappers pour les échanges API.
     Tests unitaires (JUnit/Mockito) obligatoires pour les nouvelles règles métier du Service.

