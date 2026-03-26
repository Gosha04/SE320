package com.SE320.therapy.config;

import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.SE320.therapy.cli.commands.UserCommands;

@Configuration
public class CliConfig {

    @Bean
    Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    Supplier<UUID> currentUserIdSupplier(UserCommands userCommands) {
        return userCommands::getCurrentUserId;
    }
}
