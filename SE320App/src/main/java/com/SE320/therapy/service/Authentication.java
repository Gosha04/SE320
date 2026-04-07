package com.SE320.therapy.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private static final Logger log = LoggerFactory.getLogger(Authentication.class);

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
        log.info("Registering user with email={} and userType={}", request.email(), request.userType());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration blocked because email is already registered: {}", request.email());
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
        log.info("User registered successfully with userId={} and email={}", saved.getId(), saved.getEmail());
        return issueTokens(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("email and password are required");
        }
        log.info("Attempting login for email={}", request.email());

        User user = authenticateCredentials(request.email(), request.password());
        setUserOnline(user, true);
        log.info("Login succeeded for userId={} and email={}", user.getId(), user.getEmail());
        return issueTokens(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken is required");
        }
        log.info("Refreshing access token");

        UUID userId = refreshTokens.remove(refreshToken);
        if (userId == null) {
            log.warn("Refresh token rejected because it was not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        accessTokens.entrySet().removeIf(entry -> userId.equals(entry.getValue()));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        log.info("Token refresh succeeded for userId={}", userId);
        return issueTokens(user);
    }

    @Override
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken is required");
        }
        log.info("Logging out user from access token");

        UUID userId = accessTokens.remove(accessToken);
        if (userId == null) {
            log.warn("Logout rejected because access token was invalid");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }

        refreshTokens.entrySet().removeIf(entry -> userId.equals(entry.getValue()));
        userRepository.findById(userId).ifPresent(user -> setUserOnline(user, false));
        log.info("Logout succeeded for userId={}", userId);
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toUserResponse);
    }

    public User registerUser(UserType userType, String firstName, String lastName,
                             String email, String rawPassword, String phoneNumber) {
        if (userType == null || email == null || rawPassword == null) {
            throw new IllegalArgumentException("userType, email, and rawPassword are required");
        }
        try {
            if (userRepository.findByEmail(email).isPresent()) {
                log.warn("Registration blocked during persistence because email is already registered: {}", email);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
            }

            String hash = passwordEncoder.encode(rawPassword);
            User user = new User(UUID.randomUUID(), userType, firstName, lastName, email, phoneNumber, hash);
            User saved = userRepository.save(user);
            log.info("Persisted new user with userId={} and email={}", saved.getId(), saved.getEmail());
            return saved;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to register user for email={}", email, e);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    public boolean auth(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("email and rawPassword are required");
        }
        try {
            boolean authenticated = userRepository.findPasswordByEmail(email)
                .map(hash -> passwordEncoder.matches(rawPassword, hash))
                .orElse(false);
            if (!authenticated) {
                log.warn("Authentication failed for email={}", email);
            }
            return authenticated;
        } catch (Exception e) {
            log.error("Failed to authenticate user for email={}", email, e);
            throw new RuntimeException("Failed to authenticate user", e);
        }
    }

    public User deleteUser(UUID userId, String email, String rawPassword) {
        if (userId == null || email == null || rawPassword == null) {
            throw new IllegalArgumentException("userId, email, and rawPassword are required");
        }
        try {
            log.info("Deleting user with userId={} requested by email={}", userId, email);
            if (!auth(email, rawPassword)) {
                log.warn("Delete rejected due to invalid credentials for email={}", email);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't delete another user"));
            if (!userId.equals(requester.getId())) {
                log.warn("Delete rejected because requester email={} does not own userId={}", email, userId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't delete another user");
            }

            User toDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
            userRepository.deleteById(userId);
            log.info("Deleted user with userId={} and email={}", userId, email);
            return toDelete;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete user with userId={} and email={}", userId, email, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public User login(String email, String password, HttpServletRequest request) {
        if (email == null || password == null || request == null) {
            throw new IllegalArgumentException("email, password, and request are required");
        }
        try {
            log.info("Attempting session login for email={}", email);
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
            log.info("Session login succeeded for userId={} and email={}", user.getId(), user.getEmail());
            return user;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed session login for email={}", email, e);
            throw new RuntimeException("Failed to log in user", e);
        }
    }

    public void logout(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        try {
            log.info("Logging out current HTTP session");
            Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

            if (principal instanceof AuthenticatedUser authenticatedUser) {
                log.info("Found authenticated session for userId={}", authenticatedUser.id());
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
            log.info("HTTP session logout completed");
        } catch (Exception e) {
            log.error("Failed to log out current HTTP session", e);
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
            .orElseThrow(() -> {
                log.warn("Authentication failed because email was not found: {}", email);
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email");
            });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Authentication failed because password did not match for email={}", email);
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
        log.info("Issued new access and refresh tokens for userId={}", user.getId());

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
            log.info("Updated online status for userId={} to {}", user.getId(), online);
        }
    }
}
