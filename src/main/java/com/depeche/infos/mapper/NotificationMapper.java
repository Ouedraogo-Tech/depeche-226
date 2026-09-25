package com.depeche.infos.mapper;

import com.depeche.infos.dto.NotificationResponseDTO;
import com.depeche.infos.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        if (notification == null) return null;
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setLue(notification.isLue());
        dto.setDateCreation(notification.getDateCreation());
        dto.setArticleId(notification.getArticleId());
        return dto;
    }
}
