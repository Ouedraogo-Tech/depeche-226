package com.depeche.infos.securitry;


import com.depeche.infos.entity.Permission;
import com.depeche.infos.entity.Role;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.repository.UtilisateurRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UtilisateurUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email));

        Set<Permission> permissions = new HashSet<>();
        for (Role role : utilisateur.getRoles()) {
            permissions.addAll(role.getPermissions());
        }

        Collection<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());

        return new User(
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                utilisateur.isActif(),
                true, true, true,
                authorities
        );
    }
}
