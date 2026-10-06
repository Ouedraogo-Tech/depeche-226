package com.depeche.infos.security;

import com.depeche.infos.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UtilisateurUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // Correction : un token expiré, mal formé ou dont l'utilisateur n'existe plus faisait planter
        // la requête (erreur 500). Maintenant on ignore simplement le token :
        // - route publique -> la requête passe normalement
        // - route protégée -> Spring renvoie 401 (voir authenticationEntryPoint dans SecurityConfig)
        try {
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Correction : un compte suspendu (isEnabled() == false) ne peut plus utiliser un ancien token
                // Correction : si l'admin a changé le rôle du compte, le token (qui contient les anciennes
                // permissions) n'est plus accepté → 401 → le site renvoie vers /connexion avec le bon rôle
                if (jwtService.isTokenValid(jwt, userDetails) && userDetails.isEnabled()
                        && permissionsInchangees(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    // Les permissions écrites dans le token à la connexion sont-elles toujours celles du compte ?
    private boolean permissionsInchangees(String jwt, UserDetails userDetails) {
        Object dansLeToken = jwtService.extractClaim(jwt, claims -> claims.get("permissions"));
        if (!(dansLeToken instanceof Collection<?> liste)) {
            return true; // ancien token sans cette information : on ne bloque pas
        }
        Set<String> duToken = liste.stream().map(String::valueOf).collect(Collectors.toSet());
        Set<String> actuelles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return duToken.equals(actuelles);
    }
}
