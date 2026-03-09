package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;

public class ViewSessionLibraryCommand implements Command {

    private final SessionController sessionController;

    public ViewSessionLibraryCommand(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n--- Session Library ---");
            var library = sessionController.viewSessionLibrary();

            if (library.isEmpty()) {
                System.out.println("No CBT sessions are currently available.");
                ;
                return;
            }

            for (int i = 0; i < library.size(); i++) {
                System.out.println((i + 1) + ". " + library.get(i));
            }
        } catch (Exception e) {
            System.out.println("Unable to load the session library. Please try again.");
        }
    }
}