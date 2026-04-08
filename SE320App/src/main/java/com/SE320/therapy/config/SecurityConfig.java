package com.SE320.therapy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.ApiErrorEnvelope;
import com.SE320.therapy.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {
    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/register",
                    "/auth/login",
                    "/auth/refresh",
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers("/sessions/**").hasAnyRole("PATIENT", "DOCTOR")
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(
                        response.getOutputStream(),
                        new ApiErrorEnvelope(new ApiErrorResponse(
                            "UNAUTHORIZED",
                            "Authentication is required to access this resource.",
                            java.util.List.of(),
                            java.time.Instant.now()
                        ))
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(
                        response.getOutputStream(),
                        new ApiErrorEnvelope(new ApiErrorResponse(
                            "FORBIDDEN",
                            "You do not have permission to access this resource.",
                            java.util.List.of(new ApiErrorDetail("authorization", "Access is denied")),
                            java.time.Instant.now()
                        ))
                    );
                })
            )
            .build();
    }
}
