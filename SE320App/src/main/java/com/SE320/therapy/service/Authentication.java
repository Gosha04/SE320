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
    
    public Authentication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserType userType, String firstName, String lastName,
                             String email, String rawPassword, Integer phoneNumber) {
        String hash = passwordEncoder.encode(rawPassword);
        User user = new User(UUID.randomUUID(), userType, firstName, lastName, email, phoneNumber, hash);
        userRepository.save(user);
    }

    public boolean auth(String email, String rawPassword) {
        return userRepository.findPasswordByEmail(email)
            .map(hash -> passwordEncoder.matches(rawPassword, hash))
            .orElse(false);
    }
}