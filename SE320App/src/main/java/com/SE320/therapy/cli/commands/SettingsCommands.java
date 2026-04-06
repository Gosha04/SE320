package com.SE320.therapy.cli.commands;

import org.springframework.stereotype.Component;

@Component
public class SettingsCommands implements Command {
    
    public void execute() {
        boolean running = true;
        while(running) {
            running = false;
            System.out.println("We don't have any settings for the CLI");
        }
    }
}
