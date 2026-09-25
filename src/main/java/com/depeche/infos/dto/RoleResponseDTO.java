package com.depeche.infos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleResponseDTO {
    private Long id;
    private String nom;
    private List<String> permissions;
}
