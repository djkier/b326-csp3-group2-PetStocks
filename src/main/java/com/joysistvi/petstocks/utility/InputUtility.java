package com.joysistvi.petstocks.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

    public static LocalDateTime readDateTimeSelection(Scanner scanner) {
        DateTimeFormatter displayFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

        while (true) {
            LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
            System.out.println("[1] Now - " + currentDateTime.format(displayFormatter));
            System.out.println("[2] Custom Date and Time");
            System.out.println("[0] Cancel");
            int choice = readInt(scanner, "Choice: ");

            switch (choice) {
                case 1:
                    return currentDateTime;
                case 2:
                    return readCustomDateTime(scanner);
                case 0:
                    return null;
                default:
                    System.out.println("Invalid menu selection. Choose 0, 1, or 2.");
            }
        }
    }

    private static LocalDateTime readCustomDateTime(Scanner scanner) {
        int month = readIntInRange(scanner, "Month (1-12): ", 1, 12,
                "Month must be between 1 and 12.");
        int day = readIntInRange(scanner, "Day: ", 1, 31,
                "Day must be between 1 and 31.");
        int year = readIntInRange(scanner, "Year: ", 1, 9999,
                "Year must be between 1 and 9999.");

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        while (day > daysInMonth) {
            System.out.printf("Day must be between 1 and %d for the selected month and year.%n",
                    daysInMonth);
            day = readIntInRange(scanner, "Day: ", 1, daysInMonth,
                    "Day must be valid for the selected month and year.");
        }

        int hour = readIntInRange(scanner, "Hour (1-12): ", 1, 12,
                "Hour must be between 1 and 12.");
        int minute = readIntInRange(scanner, "Minute (0-59): ", 0, 59,
                "Minute must be between 0 and 59.");

        System.out.println("[1] AM");
        System.out.println("[2] PM");
        int period = readIntInRange(scanner, "Choice: ", 1, 2,
                "Choose 1 for AM or 2 for PM.");

        int hourOfDay = hour % 12;
        if (period == 2) {
            hourOfDay += 12;
        }
        return LocalDateTime.of(year, month, day, hourOfDay, minute);
    }

    private static int readIntInRange(Scanner scanner, String prompt, int minimum,
                                      int maximum, String validationMessage) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value >= minimum && value <= maximum) {
                return value;
            }
            System.out.println(validationMessage);
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
