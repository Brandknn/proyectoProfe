package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración para ignorar completamente la seguridad en recursos estáticos
    @Bean
    public org.springframework.security.web.firewall.HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        org.springframework.security.web.firewall.StrictHttpFirewall firewall = new org.springframework.security.web.firewall.StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz

                        .requestMatchers(
                                "/",
                                "/login",
                                "/CrearCuentaMedico",
                                "/agregarMedico",
                                "/registro",
                                "/dictamen",
                                "/gestionCitas",
                                "/horario",
                                "/PerfilMedicoAjustes",
                                "/paciente",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/completar-registro-google")
                        .permitAll()

                        // Permitir acceso a recursos estáticos (CSS, JS, imágenes)
                        .requestMatchers(
                                "/*.css",
                                "/*.js",
                                "/*.png",
                                "/*.jpg",
                                "/*.jpeg",
                                "/*.gif",
                                "/*.svg",
                                "/*.ico")
                        .permitAll()

                        .anyRequest().permitAll())
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/oauth2/home", true)
                        .failureHandler(oauth2FailureHandler()))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler oauth2FailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException {
                String details = exception.getMessage();
                try {
                    if (exception instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException ex) {
                        var err = ex.getError();
                        details = "code=" + err.getErrorCode() + ", desc=" + String.valueOf(err.getDescription())
                                + ", uri=" + String.valueOf(err.getUri());
                    }
                } catch (Throwable ignored) {
                }
                System.err.println("[OAuth2 Failure] " + exception.getClass().getSimpleName() + " -> " + details);

                // Limpiar sesión OAuth2
                var session = request.getSession(false);
                if (session != null) {
                    session.removeAttribute("oauthEmail");
                    session.removeAttribute("oauthName");
                    session.removeAttribute("oauthGoogleId");
                    session.invalidate();
                }

                response.sendRedirect("/login?error=oauth2");
            }
        };
    }
}
