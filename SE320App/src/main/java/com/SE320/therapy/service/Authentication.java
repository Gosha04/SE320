package com.SE320.therapy.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SE320.therapy.objects.UserType;

@Service
public class Authentication {
    private static final String REGISTER_USER_SQL =
            "INSERT INTO users (id, user_type, first_name, last_name, username, email, password_hash, phone_number) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_PASSWORD_SQL =
            "SELECT users.password_hash FROM users WHERE users.username = ?";

    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    public Authentication(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void registerUser(UserType userType, String firstName, String lastName,
                             String username, String email, String password, Integer phoneNumber) {
        UUID userId = UUID.randomUUID();
        String passwordHash = hashPassword(password);

        try (Connection connection = dataSource.getConnection();
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

    public boolean authenticate(String username, String rawPassword) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PASSWORD_SQL)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
                String storedHash = resultSet.getString("password_hash");
                return checkPassword(rawPassword, storedHash);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to get password from database.", e);
        }
    }

    private String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private boolean checkPassword(String rawPassword, String storedHash) {
        return passwordEncoder.matches(rawPassword, storedHash);
    }
}
