package com.joysistvi.petstocks.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if ("0".equals(input)) {
                return null;
            }

            LocalDate date = parseDateOrNull(input);
            if (date != null) {
                return date;
            }
            System.out.println("Enter a valid date in YYYY-MM-DD format.");
        }
    }

    public static LocalDate parseDateOrNull(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDateTime readDateTimeOrNow(Scanner scanner, String prompt,
                                                   DateTimeFormatter formatter,
                                                   String formatHint) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if ("0".equals(input)) {
                return null;
            }
            if (input.isEmpty()) {
                return LocalDateTime.now();
            }

            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Enter a valid date and time in " + formatHint + " format.");
            }
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
