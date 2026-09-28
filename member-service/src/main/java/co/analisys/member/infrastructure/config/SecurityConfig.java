package co.analisys.member.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Microservicio de Miembros como Resource Server de OAuth2.
 * Consulta de miembros: cualquier rol autenticado.
 * Registro de un miembro nuevo: solo personal (ADMIN o TRAINER).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final KeycloakRoleConverter keycloakRoleConverter;

    public SecurityConfig(KeycloakRoleConverter keycloakRoleConverter) {
        this.keycloakRoleConverter = keycloakRoleConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Regla específica de /payments PRIMERO: es información financiera de
                // TODOS los miembros, no debe quedar cubierta por el permitAll de MEMBER
                // que aplica más abajo a "/api/members/**" en general.
                .requestMatchers(HttpMethod.GET, "/api/members/payments/**")
                    .hasAnyRole("ADMIN", "TRAINER")
                // Un MEMBER puede registrar sus propios datos de entrenamiento (Kafka).
                .requestMatchers(HttpMethod.POST, "/api/members/*/training-data")
                    .hasAnyRole("ADMIN", "TRAINER", "MEMBER")
                .requestMatchers(HttpMethod.POST, "/api/members/*/payments")
                    .hasAnyRole("ADMIN", "TRAINER")
                .requestMatchers(HttpMethod.GET, "/api/members/**")
                    .hasAnyRole("ADMIN", "TRAINER", "MEMBER")
                .requestMatchers(HttpMethod.POST, "/api/members/**")
                    .hasAnyRole("ADMIN", "TRAINER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakRoleConverter))
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
