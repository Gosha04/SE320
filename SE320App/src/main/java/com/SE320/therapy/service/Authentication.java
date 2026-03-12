package com.SE320.therapy.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SE320.therapy.objects.User;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.repository.UserRepository;

@Service
public class Authentication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Authentication(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserType userType, String firstName, String lastName,
                             String email, String rawPassword, Integer phoneNumber) {
        if (userType == null || email == null || rawPassword == null) {
            throw new IllegalArgumentException("userType, email, and rawPassword are required");
        }
        try {
            String hash = passwordEncoder.encode(rawPassword);
            User user = new User(UUID.randomUUID(), userType, firstName, lastName, email, phoneNumber, hash);
            return userRepository.save(user);
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
                throw new IllegalAccessError("Invalid credentials");
            }

            User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalAccessError("Can't delete another user"));
            if (!userId.equals(requester.getId())) {
                throw new IllegalAccessError("Can't delete another user");
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

    public void login(User user, String password) { // Needs server integration??
        if (user == null) {
            throw new IllegalArgumentException("user is required");
        }
        try {
            if (!auth(user.getEmail(), password)) {
                throw new IllegalAccessError("Wrong Password entered");
            }
            if (user.getOnline() == user.OFFLINE) {
                user.setOnline(user.ONLINE);
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to log in user", e);
        }
    }

     public void logout(User user) { // Needs server integration??
        if (user == null) {
            throw new IllegalArgumentException("user is required");
        }
        try {
            if (user.getOnline() == user.ONLINE) {
                user.setOnline(user.OFFLINE);
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to log out user", e);
        }
    }
}
