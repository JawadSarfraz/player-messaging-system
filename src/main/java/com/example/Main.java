package com.example;

/**
 * Main entry point for player messaging system.
 */
public class Main {
    public static void main(String[] args) {
        // Create and start game controller
        try {
            GameController controller = new GameController();
            controller.start();
        } catch (Exception e) {
            System.err.println("Error running game: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
