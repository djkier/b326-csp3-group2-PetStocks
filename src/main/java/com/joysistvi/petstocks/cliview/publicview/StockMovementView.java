package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.controller.DispatchController;
import com.joysistvi.petstocks.controller.RestockController;
import com.joysistvi.petstocks.model.Dispatch;
import com.joysistvi.petstocks.model.Restock;
import com.joysistvi.petstocks.utility.CliViewUtility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class StockMovementView {
    private static final int RECORDS_PER_PAGE = 10;
    private static final DateTimeFormatter DATE_TIME_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RestockController restockController;
    private final DispatchController dispatchController;
    private final Scanner scanner;

    public StockMovementView(RestockController restockController,
                             DispatchController dispatchController,
                             Scanner scanner) {
        this.restockController = restockController;
        this.dispatchController = dispatchController;
        this.scanner = scanner;
    }

    public void run() {
        List<StockMovement> allMovements = loadAllMovements();
        MovementFilter filter = MovementFilter.ALL;
        int currentPage = 0;

        while (true) {
            List<StockMovement> filteredMovements = filterMovements(allMovements, filter);
            int totalPages = calculateTotalPages(filteredMovements.size());
            currentPage = Math.min(currentPage, totalPages - 1);

            printPage(filteredMovements, filter, currentPage, totalPages);
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> {
                    filter = MovementFilter.ALL;
                    currentPage = 0;
                }
                case "2" -> {
                    filter = MovementFilter.STOCK_IN;
                    currentPage = 0;
                }
                case "3" -> {
                    filter = MovementFilter.STOCK_OUT;
                    currentPage = 0;
                }
                case "P" -> {
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("Already on the first page.");
                    }
                }
                case "N" -> {
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                    } else {
                        System.out.println("Already on the last page.");
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid selection.");
            }
        }
    }

    private List<StockMovement> loadAllMovements() {
        List<StockMovement> movements = new ArrayList<>();

        for (Restock restock : restockController.handleViewRestockHistory("date")) {
            movements.add(new StockMovement(
                    restock.getDatetimeDelivered(),
                    MovementType.STOCK_IN,
                    restock.getInventory().getProduct().getName(),
                    restock.getInventory().getBatchCode(),
                    restock.getQuantityDelivered(),
                    restock.getUsername()));
        }

        for (Dispatch dispatch : dispatchController.handleViewDispatchHistory("date")) {
            movements.add(new StockMovement(
                    dispatch.getDatetimeDispatched(),
                    MovementType.STOCK_OUT,
                    dispatch.getInventory().getProduct().getName(),
                    dispatch.getInventory().getBatchCode(),
                    dispatch.getQuantityDispatched(),
                    dispatch.getUsername()));
        }

        movements.sort(Comparator.comparing(StockMovement::dateTime).reversed());
        return movements;
    }

    private List<StockMovement> filterMovements(List<StockMovement> movements,
                                                 MovementFilter filter) {
        if (filter == MovementFilter.ALL) {
            return movements;
        }

        MovementType movementType = filter == MovementFilter.STOCK_IN
                ? MovementType.STOCK_IN : MovementType.STOCK_OUT;
        return movements.stream()
                .filter(movement -> movement.type() == movementType)
                .toList();
    }

    private int calculateTotalPages(int recordCount) {
        return Math.max(1, (recordCount + RECORDS_PER_PAGE - 1) / RECORDS_PER_PAGE);
    }

    private void printPage(List<StockMovement> movements, MovementFilter filter,
                           int currentPage, int totalPages) {
        CliViewUtility.showHeader("Stock Movement History");
        System.out.println("Filter: " + filter.displayName());
        System.out.println();

        int fromIndex = currentPage * RECORDS_PER_PAGE;
        int toIndex = Math.min(fromIndex + RECORDS_PER_PAGE, movements.size());
        printMovements(movements.subList(fromIndex, toIndex));

        System.out.println();
        System.out.printf("[P] Previous        Page %d of %d        [N] Next%n",
                currentPage + 1, totalPages);
        System.out.println("[1] All    [2] Stock In    [3] Stock Out    [0] Back");
    }

    private void printMovements(List<StockMovement> movements) {
        if (movements.isEmpty()) {
            System.out.println("No stock movements found.");
            return;
        }

        String border = "+" + "-".repeat(18) + "+" + "-".repeat(11)
                + "+" + "-".repeat(27) + "+" + "-".repeat(18)
                + "+" + "-".repeat(10) + "+" + "-".repeat(20) + "+";
        String rowFormat = "| %-16s | %-9s | %-25s | %-16s | %-8s | %-18s |%n";

        System.out.println(border);
        System.out.printf(rowFormat,
                "Date / Time", "Type", "Product", "Batch Code", "Quantity", "User");
        System.out.println(border);
        for (StockMovement movement : movements) {
            System.out.printf(rowFormat,
                    movement.dateTime().format(DATE_TIME_DISPLAY),
                    movement.type().displayName(),
                    CliViewUtility.truncate(movement.product(), 25),
                    CliViewUtility.truncate(movement.batchCode(), 16),
                    movement.quantity(),
                    CliViewUtility.truncate(movement.username(), 18));
        }
        System.out.println(border);
    }

    private enum MovementFilter {
        ALL("All"),
        STOCK_IN("Stock In"),
        STOCK_OUT("Stock Out");

        private final String displayName;

        MovementFilter(String displayName) {
            this.displayName = displayName;
        }

        private String displayName() {
            return displayName;
        }
    }

    private enum MovementType {
        STOCK_IN("STOCK IN"),
        STOCK_OUT("STOCK OUT");

        private final String displayName;

        MovementType(String displayName) {
            this.displayName = displayName;
        }

        private String displayName() {
            return displayName;
        }
    }

    private record StockMovement(LocalDateTime dateTime, MovementType type,
                                 String product, String batchCode,
                                 int quantity, String username) {
    }
}
