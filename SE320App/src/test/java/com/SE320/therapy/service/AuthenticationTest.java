package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.entity.User;
import com.SE320.therapy.objects.UserType;
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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginStoresAuthenticatedUserInSecurityContextAndSession() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        User user = createUser(false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPasswordHash())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        User loggedInUser = authentication.login(user.getEmail(), "password123", request);

        assertSame(user, loggedInUser);
        assertTrue(user.getOnline());

        SecurityContext storedContext = (SecurityContext) request.getSession(false)
            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertNotNull(storedContext);
        assertSame(storedContext, SecurityContextHolder.getContext());
        assertNotNull(storedContext.getAuthentication());

        AuthenticatedUser principal = assertInstanceOf(
            AuthenticatedUser.class,
            storedContext.getAuthentication().getPrincipal()
        );
        assertEquals(user.getId(), principal.id());
        assertEquals(user.getEmail(), principal.email());
        assertEquals("ROLE_" + user.getUserType().name(),
            storedContext.getAuthentication().getAuthorities().iterator().next().getAuthority());

        verify(userRepository).save(user);
    }

    @Test
    void loginWithInvalidCredentialsReturnsUnauthorizedAndDoesNotCreateSession() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        User user = createUser(false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPasswordHash())).thenReturn(false);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authentication.login(user.getEmail(), "wrong-password", request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertFalse(user.getOnline());
        assertNullContext();
        assertNullSession(request);

        verify(userRepository, never()).save(user);
    }

    @Test
    void logoutClearsSecurityContextInvalidatesSessionAndMarksUserOffline() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        User user = createUser(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);

        AuthenticatedUser principal = new AuthenticatedUser(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new org.springframework.security.authentication
            .UsernamePasswordAuthenticationToken(principal, null, java.util.List.of()));
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        authentication.logout(request);

        assertFalse(user.getOnline());
        assertNullContext();
        assertTrue(session.isInvalid());

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

    private void assertNullContext() {
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void assertNullSession(MockHttpServletRequest request) {
        assertNull(request.getSession(false));
    }

    private void assertNull(Object value) {
        org.junit.jupiter.api.Assertions.assertNull(value);
    }
}
