package com.SE320.therapy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SE320.therapy.dto.DeleteRequest;
import com.SE320.therapy.dto.RegisterRequest;
import com.SE320.therapy.dto.UserResponse;
import com.SE320.therapy.objects.User;
import com.SE320.therapy.service.Authentication;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final Authentication authService;

    public UserController(Authentication authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest req) {
        User saved = authService.registerUser(
            req.userType(),
            req.firstName(),
            req.lastName(),
            req.email(),
            req.password(),
            req.phoneNumber()
        );

        return new UserResponse(
            saved.getId(),
            saved.getUserType(),
            saved.getFirstName(),
            saved.getLastName(),
            saved.getEmail(),
            saved.getPhoneNumber(),
            saved.getOnline()
        );
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse delete(@RequestBody DeleteRequest req) {
        User deleted = authService.deleteUser(
            req.userId(),
            req.email(),
            req.password()
        );

        return new UserResponse(
            deleted.getId(),
            deleted.getUserType(),
            deleted.getFirstName(),
            deleted.getLastName(),
            deleted.getEmail(),
            deleted.getPhoneNumber(),
            deleted.getOnline()
        );
    }
}
