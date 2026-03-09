package com.SE320.therapy.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.SE320.therapy.Objects.UserType;
import com.SE320.therapy.repository.DataManager;

public class Authentication {
    private static final String REGISTER_USER_SQL =
            "INSERT INTO users (id, user_type, first_name, last_name, username, email, password_hash, phone_number) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;
    private static final int PBKDF2_ITERATIONS = 65536;

    private final DataManager dataManager;

    public Authentication() {
        this.dataManager = DataManager.getInstance();
    }

    public Connection getConnection() throws SQLException {
        return dataManager.getConnection();
    }

    public void registerUser(UserType userType, String firstName, String lastName,
                             String username, String email, String password, Integer phoneNumber) {
        UUID userId = UUID.randomUUID();
        String passwordHash = hashPassword(password);

        try (Connection connection = dataManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(REGISTER_USER_SQL)) {
            statement.setString(1, userId.toString());
            statement.setString(2, userType.name());
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, username);
            statement.setString(6, email);
            statement.setString(7, passwordHash);
            if (phoneNumber == null) {
                statement.setNull(8, Types.INTEGER);
            } else {
                statement.setInt(8, phoneNumber);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to register user in database.", e);
        }
    }

    private String hashPassword(String password) { // Learned on Baeldung
        byte[] salt = new byte[SALT_BYTES];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES * 8);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to hash password.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
