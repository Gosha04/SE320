package com.SE320.therapy.controller;

import com.SE320.therapy.dto.ApiErrorEnvelope;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for registering, authenticating, refreshing, logging out, and deleting users")
public class UserController {

    private final AuthService authService;
    private final Authentication authentication;

    public UserController(AuthService authService, Authentication authentication) {
        this.authService = authService;
        this.authentication = authentication;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns access and refresh tokens for the new session.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration request", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Registration details for the new user",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
        )
        @RequestBody RegisterRequest req
    ) {
        return authService.register(req);
    }

    @Operation(summary = "Log in", description = "Authenticates a user and returns a new access token, refresh token, and user profile.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid login request", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User credentials used for authentication",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class))
        )
        @RequestBody LoginRequest req
    ) {
        return authService.login(req);
    }

    @Operation(summary = "Log out", description = "Invalidates the current access token so the authenticated user is logged out.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User logged out successfully"),
        @ApiResponse(responseCode = "400", description = "Authorization header is missing or invalid", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "401", description = "Access token is invalid or expired", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
        @Parameter(
            description = "Bearer access token. Either the raw token or the value prefixed with 'Bearer '",
            required = true
        )
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        authService.logout(extractAccessToken(authorizationHeader));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user account after confirming the user id, email, and password.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid delete request", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "401", description = "Credentials are invalid", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse delete(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User identity and credentials required to permanently delete the account",
            required = true,
            content = @Content(schema = @Schema(implementation = DeleteRequest.class))
        )
        @RequestBody DeleteRequest req
    ) {
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

    @Operation(summary = "Refresh tokens", description = "Accepts a refresh token string and returns a new access token and refresh token pair.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Refresh token is missing or invalid", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "401", description = "Refresh token is invalid or expired", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Refresh token string. The token may optionally be wrapped in quotes.",
            required = true,
            content = @Content(schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiJ9.refresh-token")))
        @RequestBody String refreshToken
    ) {
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
