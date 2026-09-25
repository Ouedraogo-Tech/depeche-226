package com.depeche.infos.service;

import com.depeche.infos.entity.Notification;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.TypeNotification;
import com.depeche.infos.exception.ActionInterditeException;
import com.depeche.infos.exception.RessourceIntrouvableException;
import com.depeche.infos.repository.NotificationRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationService(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // 1. Envoyer une notification à UN utilisateur
    public Notification notifier(Utilisateur destinataire, TypeNotification type, String message, Long articleId) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setType(type);
        notification.setMessage(message);
        notification.setArticleId(articleId);
        notification.setLue(false);
        notification.setDateCreation(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    // 2. Envoyer une notification à TOUS les utilisateurs actifs d'un rôle (ex : tous les RESPONSABLE_EDITORIAL)
    public void notifierRole(String nomRole, TypeNotification type, String message, Long articleId) {
        for (Utilisateur utilisateur : utilisateurRepository.findByRoles_Name(nomRole)) {
            if (utilisateur.isActif()) {
                notifier(utilisateur, type, message, articleId);
            }
        }
    }

    // 3. Mes notifications (les plus récentes en premier)
    public List<Notification> listerMesNotifications(String email) {
        return notificationRepository.findByDestinataireOrderByDateCreationDesc(trouverUtilisateur(email));
    }

    // 4. Nombre de notifications non lues (badge du menu)
    public long compterNonLues(String email) {
        return notificationRepository.countByDestinataireAndLueFalse(trouverUtilisateur(email));
    }

    // 5. Marquer UNE notification comme lue (uniquement si elle m'appartient)
    public Notification marquerCommeLue(Long id, String email) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Notification introuvable avec l'id : " + id));
        if (!notification.getDestinataire().getEmail().equals(email)) {
            throw new ActionInterditeException("Cette notification ne vous appartient pas.");
        }
        notification.setLue(true);
        return notificationRepository.save(notification);
    }

    // 6. Tout marquer comme lu
    @Transactional
    public void marquerToutesCommeLues(String email) {
        List<Notification> nonLues = notificationRepository.findByDestinataireAndLueFalse(trouverUtilisateur(email));
        nonLues.forEach(notification -> notification.setLue(true));
        notificationRepository.saveAll(nonLues);
    }

    private Utilisateur trouverUtilisateur(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable avec l'email : " + email));
    }
}
