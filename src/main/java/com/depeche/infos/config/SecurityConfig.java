package com.depeche.infos.config;

import com.depeche.infos.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Origines autorisées à appeler l'API depuis un navigateur (le frontend Angular)
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    // On injecte le vigile
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // CORS : autorise le frontend Angular (voir corsConfigurationSource plus bas)
                .cors(Customizer.withDefaults())
                // JWT = pas de session côté serveur
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // On autorise les chemins de Swagger ET le fichier de config par défaut (v3)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**",
                                "/error").permitAll()
                        // Connexion + inscription d'un lecteur
                        .requestMatchers("/api/auth/**").permitAll()

                        // ===== Correction : routes du SITE PUBLIC (visiteur sans token) =====
                        .requestMatchers(HttpMethod.GET, "/api/articles/publies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/commentaires/article/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/parametres").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/newsletter/abonner", "/api/newsletter/desabonner").permitAll()
                        // Images des articles
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // Correction de sécurité : POST /api/utilisateurs n'est plus public
                        // (il est réservé à l'admin via @PreAuthorize dans UtilisateurController)
                        .anyRequest().authenticated()
                )
                // Correction : réponses JSON claires au lieu d'un 403 vide
                .exceptionHandling(exceptions -> exceptions
                        // Pas de token / token invalide ou expiré -> 401
                        .authenticationEntryPoint((request, response, authException) ->
                                ecrireErreur(response, HttpServletResponse.SC_UNAUTHORIZED, "Non authentifié",
                                        "Vous devez être connecté (token absent, invalide ou expiré)."))
                        // Connecté mais sans la permission -> 403
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                ecrireErreur(response, HttpServletResponse.SC_FORBIDDEN, "Accès refusé",
                                        "Vous n'avez pas les droits pour effectuer cette action."))
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS : le navigateur bloque par défaut les appels d'un site (localhost:4200) vers un autre (localhost:8080).
    // Ici on autorise explicitement le frontend.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Même format JSON que GestionGlobalDesException : { "erreur": "...", "message": "..." }
    private static void ecrireErreur(HttpServletResponse response, int status, String erreur, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"erreur\":\"" + erreur + "\",\"message\":\"" + message + "\"}");
    }
}
