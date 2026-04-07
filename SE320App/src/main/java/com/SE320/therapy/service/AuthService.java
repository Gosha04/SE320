package com.SE320.therapy.service;

import com.SE320.therapy.dto.LoginRequest;
import com.SE320.therapy.dto.RegisterRequest;
import com.SE320.therapy.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String accessToken);
    Page<UserResponse> getUsers(Pageable pageable);
}
