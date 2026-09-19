package com.depeche.infos.mapper;

import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.dto.CategorieResponseDTO;
import com.depeche.infos.entity.Categorie;
import org.springframework.stereotype.Component;

@Component
public class CategorieMapper {

    public Categorie toEntity(CategorieRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Categorie categorie = new Categorie();
        categorie.setNom(requestDTO.getNom());
        categorie.setDescription(requestDTO.getDescription());
        return categorie;
    }

    public CategorieResponseDTO toResponseDTO(Categorie categorie) {
        if (categorie == null) return null;
        CategorieResponseDTO dto = new CategorieResponseDTO();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        dto.setDescription(categorie.getDescription());
        return dto;
    }
}