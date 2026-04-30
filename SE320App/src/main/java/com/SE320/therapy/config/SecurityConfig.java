package com.SE320.therapy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.ApiErrorEnvelope;
import com.SE320.therapy.dto.ApiErrorResponse;
import com.SE320.therapy.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
        ObjectProvider<ObjectMapper> objectMapperProvider,
        ObjectProvider<JwtAuthenticationFilter> jwtAuthenticationFilterProvider
    ) {
        this.objectMapper = objectMapperProvider.getIfAvailable(SecurityConfig::defaultObjectMapper);
        this.jwtAuthenticationFilter = jwtAuthenticationFilterProvider.getIfAvailable();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity configured = http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
            );

        if (jwtAuthenticationFilter != null) {
            configured = configured.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return configured
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

    private static ObjectMapper defaultObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
