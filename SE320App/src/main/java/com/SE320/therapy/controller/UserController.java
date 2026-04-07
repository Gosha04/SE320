package com.SE320.therapy.controller;

import com.SE320.therapy.dto.ApiErrorEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication", description = "Endpoints for registering, authenticating, refreshing, logging out, and deleting users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
        @Valid @RequestBody RegisterRequest req
    ) {
        log.info("Received registration request for email={}", req == null ? null : req.email());
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
        @Valid @RequestBody LoginRequest req
    ) {
        log.info("Received login request for email={}", req == null ? null : req.email());
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
        @RequestHeader("Authorization") @NotBlank(message = "Authorization header is required") String authorizationHeader
    ) {
        log.info("Received logout request");
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
        @Valid @RequestBody DeleteRequest req
    ) {
        log.info(
            "Received delete request for userId={} and email={}",
            req == null ? null : req.userId(),
            req == null ? null : req.email()
        );
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
        @RequestBody @NotBlank(message = "refreshToken is required") String refreshToken
    ) {
        log.info("Received token refresh request");
        return authService.refreshToken(normalizeToken(refreshToken));
    }

    @Operation(summary = "Get users", description = "Returns a paginated list of users.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination request", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getUsers(
        @Parameter(description = "Pagination information including page number, size, and sorting")
        Pageable pageable
    ) {
        return authService.getUsers(pageable);
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
