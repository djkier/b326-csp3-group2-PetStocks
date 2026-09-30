package com.joysistvi.petstocks.utility;

import java.util.Scanner;

public class InputUtility {
    private InputUtility() {
    }

    public static int readInt(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (RuntimeException e) {
                displayError("Please enter a valid number.");
                pressEnterToContinue(scanner);
                System.out.print("Enter a number: ");
            }
        }
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static int readOptionalInt(Scanner scanner, int currentValue) {
        while (true) {
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                return currentValue;
            }

            try {
                return Integer.parseInt(input.trim());
            } catch (RuntimeException e) {
                displayError("Please enter a valid number or press Enter to keep the current value.");
                pressEnterToContinue(scanner);
                System.out.print("Enter a number or press Enter to keep the current value: ");
            }
        }
    }

    public static int readOptionalInt(Scanner scanner, String prompt, int currentValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return currentValue;
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number or press Enter to keep.");
            }
        }
    }

    public static Integer readIntOrNull(Scanner scanner) {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static void displayError(String message) {
        System.out.println("Error: " + message);
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void pressEnterToContinue(Scanner scanner, String prompt) {
        System.out.print(prompt);
        scanner.nextLine();
    }
}
