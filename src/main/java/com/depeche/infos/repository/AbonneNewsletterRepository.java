package com.depeche.infos.repository;

import com.depeche.infos.entity.AbonneNewsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbonneNewsletterRepository extends JpaRepository<AbonneNewsletter, Long> {

    // 1. Retrouver un abonné par son email (utile pour le désabonnement)
    Optional<AbonneNewsletter> findByEmail(String email);

    // 2. Vérifier si un email est déjà abonné (pour éviter les doublons)
    boolean existsByEmail(String email);

    // 3. Récupérer tous les abonnés actifs ou inactifs
    List<AbonneNewsletter> findByActif(boolean actif);

    // 4. Tous les abonnés, les plus récents en premier (liste du Webmaster)
    List<AbonneNewsletter> findAllByOrderByDateAbonnementDesc();

    // 5. Statistiques
    long countByActif(boolean actif);
}
