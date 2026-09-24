package com.depeche.infos.security;

import com.depeche.infos.dto.AuthRequestDTO;
import com.depeche.infos.dto.AuthResponseDTO;
import com.depeche.infos.entity.Permission;
import com.depeche.infos.entity.Role;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.repository.UtilisateurRepository;
import com.depeche.infos.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthService(AuthenticationManager authenticationManager,
                        UtilisateurUserDetailsService userDetailsService,
                        JwtService jwtService,
                        UtilisateurRepository utilisateurRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // On recharge l'entité Utilisateur pour accéder à ses rôles ET permissions
        // (UserDetails.getAuthorities() ne contient que les permissions, cf. UtilisateurUserDetailsService)
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow();

        List<String> roles = utilisateur.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissions = utilisateur.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", roles);
        extraClaims.put("permissions", permissions);

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        return new AuthResponseDTO(jwtToken);
    }
}