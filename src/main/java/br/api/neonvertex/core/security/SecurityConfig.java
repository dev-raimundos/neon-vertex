package br.api.neonvertex.core.security;

import br.api.neonvertex.modules.auth.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] PUBLIC_ROUTES = {"/api/auth/login", "/api/auth/refresh", "/api/users/register",
        "/actuator/health"};

    private static final String[] DOCS_ROUTES = {"/v3/api-docs/**", "/v3/api-docs.yaml", "/webjars/**", "/scalar",
        "/scalar/**"};

    // -------------------------------------------------------------------------
    // Produção — docs bloqueados
    // -------------------------------------------------------------------------
    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
        return buildChain(http, new String[]{});
    }

    // -------------------------------------------------------------------------
    // Local — docs liberados
    // -------------------------------------------------------------------------
    @Bean
    @Profile("!prod")
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        return buildChain(http, DOCS_ROUTES);
    }

    // -------------------------------------------------------------------------
    // Shared beans
    // -------------------------------------------------------------------------
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------
    private SecurityFilterChain buildChain(HttpSecurity http, String[] extraPublicRoutes) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(
                session -> session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(PUBLIC_ROUTES).permitAll();
                if (extraPublicRoutes.length > 0) {
                    auth.requestMatchers(extraPublicRoutes).permitAll();
                }
                auth.anyRequest().authenticated();
            }).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
