package com.joysistvi.petstocks.utility;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public final class CliViewUtility {
    private static final int HEADER_WIDTH = 72;
    public static final int RECORDS_PER_PAGE = 10;

    private CliViewUtility() {
    }

    public static void showHeader(String title) {
        String normalizedTitle = title == null ? "" : title.trim();
        String displayedTitle = truncate(normalizedTitle, HEADER_WIDTH);
        int availablePadding = HEADER_WIDTH - displayedTitle.length();
        int leftPadding = availablePadding / 2;
        int rightPadding = availablePadding - leftPadding;

        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println(" ".repeat(leftPadding) + displayedTitle
                + " ".repeat(rightPadding));
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

    public static int totalPages(int recordCount) {
        return Math.max(1, (recordCount + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE);
    }

    public static int normalizePage(int currentPage, int recordCount) {
        return Math.max(0, Math.min(currentPage, totalPages(recordCount) - 1));
    }

    public static <T> List<T> page(List<T> records, int currentPage) {
        int normalizedPage = normalizePage(currentPage, records.size());
        int fromIndex = normalizedPage * RECORDS_PER_PAGE;
        int toIndex = Math.min(fromIndex + RECORDS_PER_PAGE, records.size());
        return records.subList(fromIndex, toIndex);
    }

    public static void printPagination(int currentPage, int recordCount) {
        int normalizedPage = normalizePage(currentPage, recordCount);
        System.out.println();
        System.out.printf("[P] Previous        Page %d of %d        [N] Next%n",
                normalizedPage + 1, totalPages(recordCount));
    }

    public static int previousPage(int currentPage) {
        if (currentPage > 0) {
            return currentPage - 1;
        }
        System.out.println("Already on the first page.");
        return currentPage;
    }

    public static int nextPage(int currentPage, int recordCount) {
        if (currentPage < totalPages(recordCount) - 1) {
            return currentPage + 1;
        }
        System.out.println("Already on the last page.");
        return currentPage;
    }

    public static <T> void browsePages(List<T> records, Scanner scanner,
                                       Consumer<List<T>> tablePrinter) {
        int currentPage = 0;
        while (true) {
            tablePrinter.accept(page(records, currentPage));
            printPagination(currentPage, records.size());
            if (totalPages(records.size()) == 1) {
                return;
            }

            System.out.print("Page choice ([P] Previous, [N] Next, [0] Continue): ");
            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "P" -> currentPage = previousPage(currentPage);
                case "N" -> currentPage = nextPage(currentPage, records.size());
                case "0" -> { return; }
                default -> System.out.println("Invalid page selection.");
            }
        }
    }
}
