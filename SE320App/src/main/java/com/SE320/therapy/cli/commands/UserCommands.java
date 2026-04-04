package com.SE320.therapy.cli.commands;

import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.controller.UserController;
import com.SE320.therapy.dto.DeleteRequest;
import com.SE320.therapy.dto.LoginRequest;
import com.SE320.therapy.dto.RegisterRequest;
import com.SE320.therapy.dto.UserResponse;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.service.AuthResponse;

@Component
public class UserCommands implements Command {
    private final UserController userController;
    private final Scanner scanner;

    private UserResponse currentUser;
    private String accessToken;
    private String refreshToken;

    public UserCommands(UserController userController, Scanner scanner) {
        this.userController = userController;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        boolean running = true;

        printMenu();

        while (running) {
            String choice = scanner.nextLine().trim();

            switch (choice.toLowerCase()) {
                case "register" -> handleRegister();
                case "login" -> handleLogin();
                case "logout" -> handleLogout();
                case "delete" -> handleDelete();
                case "session" -> printCurrentSession();
                case "help" -> printMenu();
                case "back" -> running = false;
                default -> System.out.println("Please choose a valid menu option.");
            }
        }
    }

    public UUID getCurrentUserId() {
        return currentUser != null ? currentUser.id() : null;
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== User Menu ===");
        System.out.println("register");
        System.out.println("login");
        System.out.println("logout");
        System.out.println("delete (account)");
        System.out.println("session (information)");
        System.out.println("exit\n\n");
    }

    private void handleRegister() {
        try {
            UserType userType = readUserType();
            System.out.print("First name: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            Integer phoneNumber = readPhoneNumber();

            AuthResponse response = userController.register(new RegisterRequest(
                userType,
                firstName,
                lastName,
                email,
                password,
                phoneNumber
            ));
            updateSession(response);

            System.out.println("Registration successful.");
            printUser(currentUser);
            printTokens();
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to register: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to register right now.");
        }
    }

    private void handleLogin() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            AuthResponse response = userController.login(new LoginRequest(email, password));
            updateSession(response);

            System.out.println("Login successful.");
            printUser(currentUser);
            printTokens();
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to login: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to login right now.");
        }
    }

    private void handleLogout() {
        if (accessToken == null || accessToken.isBlank()) {
            System.out.println("You are not currently logged in.");
            return;
        }

        try {
            if (!refreshSessionIfPossible()) {
                return;
            }

            userController.logout("Bearer " + accessToken);
            clearSession();
            System.out.println("Logout successful.");
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to logout: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to logout right now.");
        }
    }

    private void handleDelete() {
        UUID userId = currentUser != null ? currentUser.id() : null;
        if (userId == null) {
            System.out.println("You must be logged in to delete your account.");
            return;
        }

        try {
            if (!refreshSessionIfPossible()) {
                return;
            }

            System.out.print("Confirm email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Confirm password: ");
            String password = scanner.nextLine();

            UserResponse deletedUser = userController.delete(new DeleteRequest(userId, email, password));
            clearSession();

            System.out.println("Account deleted successfully.");
            printUser(deletedUser);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to delete account: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to delete account right now.");
        }
    }

    private void printCurrentSession() {
        if (currentUser == null) {
            System.out.println("No user is currently authenticated in the CLI session.");
            return;
        }

        if (!refreshSessionIfPossible()) {
            return;
        }

        printUser(currentUser);
        printTokens();
    }

    private UserType readUserType() {
        while (true) {
            System.out.print("User type (PATIENT, DOCTOR, ADMIN): ");
            String input = scanner.nextLine().trim();

            try {
                return UserType.valueOf(input.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                System.out.println("Please enter PATIENT, DOCTOR, or ADMIN.");
            }
        }
    }

    private Integer readPhoneNumber() {
        while (true) {
            System.out.print("Phone number: ");
            String input = scanner.nextLine().trim();

            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException ignored) {
                System.out.println("Please enter a valid phone number.");
            }
        }
    }

    private void updateSession(AuthResponse response) {
        currentUser = response.user();
        accessToken = response.accessToken();
        refreshToken = response.refreshToken();
    }

    private void clearSession() {
        currentUser = null;
        accessToken = null;
        refreshToken = null;
    }

    private boolean refreshSessionIfPossible() {
        if (currentUser == null) {
            return true;
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            System.out.println("Your session can no longer be refreshed. Please log in again.");
            clearSession();
            return false;
        }

        try {
            AuthResponse response = userController.refresh(refreshToken);
            updateSession(response);
            return true;
        } catch (IllegalArgumentException | ResponseStatusException e) {
            clearSession();
            System.out.println("Your session expired. Please log in again. " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Unable to refresh your session right now.");
            return false;
        }
    }

    private void printUser(UserResponse user) {
        System.out.println("User ID: " + user.id());
        System.out.println("Role: " + user.userType());
        System.out.println("Name: " + user.firstName() + " " + user.lastName());
        System.out.println("Email: " + user.email());
        System.out.println("Phone: " + user.phoneNumber());
        System.out.println("Online: " + user.online());
    }

    private void printTokens() {
        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);
    }
}
