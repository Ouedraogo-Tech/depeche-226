package com.depeche.infos.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer ";
    public AuthResponseDTO(String token) {
        this.token = token;
    }
}