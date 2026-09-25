package com.depeche.infos.dto;

import com.depeche.infos.enums.TypeNotification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private TypeNotification type;
    private boolean lue;
    private LocalDateTime dateCreation;
    private Long articleId;
}
