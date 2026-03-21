package com.SE320.therapy.service;

import com.SE320.therapy.dto.LoginRequest;
import com.SE320.therapy.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String accessToken);
}