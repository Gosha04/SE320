package com.SE320.therapy;

import com.SE320.therapy.cli.commands.Menu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "app.cli.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner runCli(Menu menu) {
        return args -> menu.execute();
    }
}
