package com.SE320.therapy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SE320.therapy.dto.DeleteRequest;
import com.SE320.therapy.dto.LoginRequest;
import com.SE320.therapy.dto.RegisterRequest;
import com.SE320.therapy.dto.UserResponse;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.service.AuthResponse;
import com.SE320.therapy.service.AuthService;
import com.SE320.therapy.service.Authentication;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final AuthService authService;
    private final Authentication authentication;

    public UserController(AuthService authService, Authentication authentication) {
        this.authService = authService;
        this.authentication = authentication;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(extractAccessToken(authorizationHeader));
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse delete(@RequestBody DeleteRequest req) {
        User deleted = authentication.deleteUser(
            req.userId(),
            req.email(),
            req.password()
        );

        return new UserResponse(
            deleted.getId(),
            deleted.getUserType(),
            deleted.getFirstName(),
            deleted.getLastName(),
            deleted.getEmail(),
            deleted.getPhoneNumber(),
            deleted.getOnline()
        );
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(@RequestBody String refreshToken) {
        return authService.refreshToken(normalizeToken(refreshToken));
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Authorization header is required");
        }

        String trimmedHeader = authorizationHeader.trim();
        if (trimmedHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmedHeader.substring(7).trim();
        }

        return trimmedHeader;
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return null;
        }

        String normalized = token.trim();
        if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
            return normalized.substring(1, normalized.length() - 1);
        }

        return normalized;
    }
}
