package com.depeche.infos.controller;

import com.depeche.infos.dto.NotificationResponseDTO;
import com.depeche.infos.mapper.NotificationMapper;
import com.depeche.infos.security.UtilisateurConnecte;
import com.depeche.infos.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Notifications de l'utilisateur connecté (tout utilisateur connecté, chacun ne voit que les siennes).
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    // GET /api/notifications : mes notifications, les plus récentes en premier
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> mesNotifications() {
        List<NotificationResponseDTO> dtos = notificationService.listerMesNotifications(UtilisateurConnecte.email()).stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/notifications/non-lues : { "nombre": 2 } (badge du menu / carte du dashboard)
    @GetMapping("/non-lues")
    public ResponseEntity<Map<String, Long>> nombreNonLues() {
        return ResponseEntity.ok(Map.of("nombre", notificationService.compterNonLues(UtilisateurConnecte.email())));
    }

    // PATCH /api/notifications/1/lue
    @PatchMapping("/{id}/lue")
    public ResponseEntity<NotificationResponseDTO> marquerCommeLue(@PathVariable Long id) {
        return ResponseEntity.ok(notificationMapper.toResponseDTO(
                notificationService.marquerCommeLue(id, UtilisateurConnecte.email())));
    }

    // PATCH /api/notifications/lire-tout
    @PatchMapping("/lire-tout")
    public ResponseEntity<Void> toutMarquerCommeLu() {
        notificationService.marquerToutesCommeLues(UtilisateurConnecte.email());
        return ResponseEntity.noContent().build();
    }
}
