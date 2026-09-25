package com.depeche.infos.controller;

import com.depeche.infos.dto.RoleResponseDTO;
import com.depeche.infos.entity.Permission;
import com.depeche.infos.entity.Role;
import com.depeche.infos.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final UtilisateurService utilisateurService;

    public RoleController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // GET /api/roles : rôles et leurs permissions (carte "Rôles & compétences" de l'espace Administrateur)
    @PreAuthorize("hasAuthority('ROLE_ATTRIBUER')")
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> listerRoles() {
        return ResponseEntity.ok(utilisateurService.listerRoles().stream().map(this::versDTO).toList());
    }

    private RoleResponseDTO versDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setNom(role.getName());
        dto.setPermissions(role.getPermissions().stream().map(Permission::getName).sorted().toList());
        return dto;
    }
}
