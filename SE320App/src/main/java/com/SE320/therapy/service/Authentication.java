package com.SE320.therapy.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.dto.LoginRequest;
import com.SE320.therapy.dto.RegisterRequest;
import com.SE320.therapy.dto.UserResponse;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.repository.UserRepository;

@Service
public class Authentication implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, UUID> accessTokens = new ConcurrentHashMap<>();
    private final Map<String, UUID> refreshTokens = new ConcurrentHashMap<>();
    
    public Authentication(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User saved = registerUser(
            request.userType(),
            request.firstName(),
            request.lastName(),
            request.email(),
            request.password(),
            request.phoneNumber()
        );
        setUserOnline(saved, true);
        return issueTokens(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("email and password are required");
        }

        User user = authenticateCredentials(request.email(), request.password());
        setUserOnline(user, true);
        return issueTokens(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        UUID userId = refreshTokens.remove(refreshToken);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        accessTokens.entrySet().removeIf(entry -> userId.equals(entry.getValue()));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return issueTokens(user);
    }

    @Override
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken is required");
        }

        UUID userId = accessTokens.remove(accessToken);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }

        refreshTokens.entrySet().removeIf(entry -> userId.equals(entry.getValue()));
        userRepository.findById(userId).ifPresent(user -> setUserOnline(user, false));
    }

    public User registerUser(UserType userType, String firstName, String lastName,
                             String email, String rawPassword, String phoneNumber) {
        if (userType == null || email == null || rawPassword == null) {
            throw new IllegalArgumentException("userType, email, and rawPassword are required");
        }
        try {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
            }

            String hash = passwordEncoder.encode(rawPassword);
            User user = new User(UUID.randomUUID(), userType, firstName, lastName, email, phoneNumber, hash);
            return userRepository.save(user);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    public boolean auth(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("email and rawPassword are required");
        }
        try {
            return userRepository.findPasswordByEmail(email)
                .map(hash -> passwordEncoder.matches(rawPassword, hash))
                .orElse(false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate user", e);
        }
    }

    public User deleteUser(UUID userId, String email, String rawPassword) {
        if (userId == null || email == null || rawPassword == null) {
            throw new IllegalArgumentException("userId, email, and rawPassword are required");
        }
        try {
            if (!auth(email, rawPassword)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't delete another user"));
            if (!userId.equals(requester.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't delete another user");
            }

            User toDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            userRepository.deleteById(userId);
            return toDelete;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public User login(String email, String password, HttpServletRequest request) {
        if (email == null || password == null || request == null) {
            throw new IllegalArgumentException("email, password, and request are required");
        }
        try {
            User user = authenticateCredentials(email, password);
            setUserOnline(user, true);
            issueTokens(user);

            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
            );
            AuthenticatedUser principal = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType()
            );

            UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
            return user;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to log in user", e);
        }
    }

    public void logout(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

            if (principal instanceof AuthenticatedUser authenticatedUser) {
                userRepository.findById(authenticatedUser.id()).ifPresent(user -> {
                    setUserOnline(user, false);
                    accessTokens.entrySet().removeIf(entry -> authenticatedUser.id().equals(entry.getValue()));
                    refreshTokens.entrySet().removeIf(entry -> authenticatedUser.id().equals(entry.getValue()));
                });
            }

            HttpSession session = request.getSession(false);
            SecurityContextHolder.clearContext();
            if (session != null) {
                session.invalidate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to log out user", e);
        }
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null
            || request.userType() == null
            || request.email() == null
            || request.password() == null) {
            throw new IllegalArgumentException("userType, email, and password are required");
        }
    }

    private User authenticateCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return user;
    }

    private AuthResponse issueTokens(User user) {
        accessTokens.entrySet().removeIf(entry -> user.getId().equals(entry.getValue()));
        refreshTokens.entrySet().removeIf(entry -> user.getId().equals(entry.getValue()));

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        accessTokens.put(accessToken, user.getId());
        refreshTokens.put(refreshToken, user.getId());

        return new AuthResponse(
            accessToken,
            refreshToken,
            toUserResponse(user)
        );
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUserType(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getOnline()
        );
    }

    private void setUserOnline(User user, boolean online) {
        if (user.getOnline() != online) {
            user.setOnline(online);
            userRepository.save(user);
        }
    }
}
