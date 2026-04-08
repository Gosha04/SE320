package com.SE320.therapy.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.SE320.therapy.entity.User;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.service.AuthenticatedUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {

    private final ObjectMapper objectMapper;
    private final byte[] signingKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtService(
        ObjectMapper objectMapper,
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.expiration:3600000}") long accessTokenExpirationMs,
        @Value("${jwt.refresh-expiration:86400000}") long refreshTokenExpirationMs
    ) {
        this.objectMapper = objectMapper;
        this.signingKey = normalizeSecret(jwtSecret);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(User user) {
        return createToken(user, "access", accessTokenExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return createToken(user, "refresh", refreshTokenExpirationMs);
    }

    public TokenClaims parseAccessToken(String token) {
        return parseAndValidate(token, "access");
    }

    public TokenClaims parseRefreshToken(String token) {
        return parseAndValidate(token, "refresh");
    }

    public Instant extractExpiration(String token) {
        return parseAndValidate(token, null).expiresAt();
    }

    private String createToken(User user, String tokenType, long ttlMs) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(ttlMs);

        Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("userType", user.getUserType().name());
        payload.put("type", tokenType);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());
        payload.put("jti", UUID.randomUUID().toString());

        try {
            String headerPart = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String payloadPart = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String signaturePart = sign(headerPart + "." + payloadPart);
            return headerPart + "." + payloadPart + "." + signaturePart;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create JWT", ex);
        }
    }

    private TokenClaims parseAndValidate(String token, String expectedType) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token format");
        }

        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new IllegalArgumentException("Invalid token signature");
        }

        try {
            JsonNode root = objectMapper.readTree(base64UrlDecode(parts[1]));
            long expEpochSeconds = root.path("exp").asLong(0L);
            if (expEpochSeconds <= 0L) {
                throw new IllegalArgumentException("Token expiration is missing");
            }

            Instant expiresAt = Instant.ofEpochSecond(expEpochSeconds);
            if (Instant.now().isAfter(expiresAt)) {
                throw new IllegalArgumentException("Token has expired");
            }

            String tokenType = root.path("type").asText("");
            if (expectedType != null && !expectedType.equals(tokenType)) {
                throw new IllegalArgumentException("Unexpected token type");
            }

            UUID userId = UUID.fromString(root.path("sub").asText());
            String email = root.path("email").asText(null);
            String firstName = root.path("firstName").asText(null);
            String lastName = root.path("lastName").asText(null);
            UserType userType = UserType.valueOf(root.path("userType").asText());

            return new TokenClaims(
                userId,
                email,
                firstName,
                lastName,
                userType,
                tokenType,
                expiresAt,
                new AuthenticatedUser(userId, email, firstName, lastName, userType)
            );
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid token payload", ex);
        }
    }

    private String sign(String input) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            byte[] signature = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to sign token", ex);
        }
    }

    private static boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(
            left.getBytes(StandardCharsets.UTF_8),
            right.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static byte[] normalizeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("jwt.secret must not be blank");
        }
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    private static String base64UrlEncode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private static byte[] base64UrlDecode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    public record TokenClaims(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        UserType userType,
        String tokenType,
        Instant expiresAt,
        AuthenticatedUser principal
    ) {}
}
