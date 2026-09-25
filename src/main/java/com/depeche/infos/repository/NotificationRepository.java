package com.depeche.infos.repository;

import com.depeche.infos.entity.Notification;
import com.depeche.infos.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Les notifications d'un utilisateur, les plus récentes en premier
    List<Notification> findByDestinataireOrderByDateCreationDesc(Utilisateur destinataire);

    // Notifications non lues (pour "tout marquer comme lu")
    List<Notification> findByDestinataireAndLueFalse(Utilisateur destinataire);

    // Nombre de notifications non lues (pour le badge du menu)
    long countByDestinataireAndLueFalse(Utilisateur destinataire);
}
