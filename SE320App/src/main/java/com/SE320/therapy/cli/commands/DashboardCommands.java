package com.SE320.therapy.cli.commands;

import java.util.Scanner;

public class DashboardCommands implements Command {
    private final Scanner scanner;

    public DashboardCommands(DashboardCommands dCommands, Scanner scanner) {
        this.scanner = scanner;
    }
 
    @Override
    public void execute() {
        boolean running = true;

        while(running) {
            String choice = scanner.nextLine().trim();
            
        }
        
    }
}
