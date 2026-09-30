package com.joysistvi.petstocks.utility;

import java.util.Scanner;

public final class CliViewUtility {
    private static final int HEADER_WIDTH = 72;

    private CliViewUtility() {
    }

    public static void showHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println(title);
        System.out.println("=".repeat(HEADER_WIDTH));
    }

    public static String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    public static String keepCurrentIfBlank(String value, String currentValue) {
        return value.trim().isEmpty() ? currentValue : value;
    }

    public static String formatArchiveStatus(boolean isArchived) {
        return isArchived ? "Archived" : "Active";
    }

    public static void pauseAfter(Scanner scanner, boolean shouldPause) {
        if (shouldPause) {
            InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
        }
    }

    public static boolean confirmExact(Scanner scanner, String prompt, String expectedValue) {
        System.out.print(prompt);
        return expectedValue.equals(scanner.nextLine());
    }
}
