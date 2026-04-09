package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.dto.LoginRequest;
import com.SE320.therapy.dto.objects.UserType;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void loginReturnsTokensAndMarksUserOnline() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        User user = createUser(false);
        LoginRequest request = new LoginRequest(user.getEmail(), "password123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPasswordHash())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        AuthResponse response = authentication.login(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals(user.getId(), response.user().id());
        assertTrue(response.user().online());
        verify(userRepository).save(user);
    }

    @Test
    void loginRejectsInvalidPassword() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        User user = createUser(false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPasswordHash())).thenReturn(false);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authentication.login(new LoginRequest(user.getEmail(), "wrong-password"))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(userRepository, never()).save(user);
    }

    @Test
    void logoutWithAccessTokenMarksUserOffline() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        User user = createUser(true);

        JwtService.TokenClaims claims = new JwtService.TokenClaims(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType(),
            "access",
            Instant.now().plusSeconds(300),
            new AuthenticatedUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserType())
        );

        when(jwtService.parseAccessToken("access-token")).thenReturn(claims);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        authentication.logout("access-token");

        assertTrue(!user.getOnline());
        verify(userRepository).save(user);
    }

    private User createUser(boolean online) {
        User user = new User(
            UUID.randomUUID(),
            UserType.PATIENT,
            "Test",
            "User",
            "test@example.com",
            "1234567890",
            "encoded-password"
        );
        user.setOnline(online);
        return user;
    }
}
