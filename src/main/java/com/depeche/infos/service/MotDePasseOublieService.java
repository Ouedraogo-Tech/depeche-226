package com.depeche.infos.service;

import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Set;

// "Mot de passe oublié" : réservé aux LECTEURS et aux ADMINISTRATEURS.
// Les journalistes, responsables éditoriaux et webmasters ne peuvent pas l'utiliser :
// leur mot de passe est attribué (et changé) par l'administrateur.
@Service
public class MotDePasseOublieService {

    private static final Logger log = LoggerFactory.getLogger(MotDePasseOublieService.class);

    // Les seuls rôles autorisés à réinitialiser eux-mêmes leur mot de passe
    private static final Set<String> ROLES_AUTORISES = Set.of("LECTEUR", "ADMIN");

    // Durée de validité du lien envoyé par e-mail
    private static final int VALIDITE_MINUTES = 30;

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom aleatoire = new SecureRandom();

    public MotDePasseOublieService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder,
                                   EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 1. Demande : si le compte existe, est actif et a le droit, on envoie un lien par e-mail.
    // Dans TOUS les cas, le contrôleur répond la même chose : on ne révèle jamais si une adresse a un compte.
    public void demanderReinitialisation(String email) {
        String emailNormalise = email.trim().toLowerCase();
        utilisateurRepository.findByEmail(emailNormalise)
                .filter(Utilisateur::isActif)
                .filter(this::estAutorise)
                .ifPresent(utilisateur -> {
                    String jeton = genererJeton();
                    // On ne garde que l'EMPREINTE du jeton : même avec un accès à la base, le lien reste inutilisable
                    utilisateur.setJetonReinitialisation(empreinte(jeton));
                    utilisateur.setExpirationReinitialisation(LocalDateTime.now().plusMinutes(VALIDITE_MINUTES));
                    utilisateurRepository.save(utilisateur);
                    try {
                        emailService.envoyerReinitialisation(utilisateur.getEmail(), utilisateur.getPrenom(), jeton);
                    } catch (Exception e) {
                        log.warn("E-mail de réinitialisation non envoyé à {} : {}", utilisateur.getEmail(), e.getMessage());
                    }
                });
    }

    // 2. Nouveau mot de passe : le jeton doit exister, ne pas être expiré, et le compte doit toujours avoir le droit
    public void reinitialiser(String jeton, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByJetonReinitialisation(empreinte(jeton.trim()))
                .filter(u -> u.getExpirationReinitialisation() != null
                        && u.getExpirationReinitialisation().isAfter(LocalDateTime.now()))
                .filter(Utilisateur::isActif)
                .filter(this::estAutorise)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ce lien n'est plus valide. Faites une nouvelle demande de mot de passe oublié."));

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        // Le lien ne sert qu'une fois
        utilisateur.setJetonReinitialisation(null);
        utilisateur.setExpirationReinitialisation(null);
        utilisateurRepository.save(utilisateur);
    }

    private boolean estAutorise(Utilisateur utilisateur) {
        return utilisateur.getRoles().stream().anyMatch(role -> ROLES_AUTORISES.contains(role.getName()));
    }

    // 32 octets aléatoires → ~43 caractères utilisables dans une adresse (impossible à deviner)
    private String genererJeton() {
        byte[] octets = new byte[32];
        aleatoire.nextBytes(octets);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
    }

    private static String empreinte(String jeton) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(jeton.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }
}
