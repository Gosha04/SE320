package com.SE320.therapy.config;

import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.SE320.therapy.service.AuthenticatedUser;

@Configuration
public class CliConfig {

    @Bean
    Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    Supplier<UUID> currentUserIdSupplier() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof AuthenticatedUser authenticatedUser) {
                return authenticatedUser.id();
            }

            return null;
        };
    }
}
