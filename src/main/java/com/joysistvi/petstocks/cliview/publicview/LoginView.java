package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class LoginView {
    private static final int HEADER_WIDTH = 40;

    private final Scanner scanner;

    public LoginView(Scanner scanner) {
        this.scanner = scanner;
    }

    public Selection promptForSelection() {
        while (true) {
            printMenu();
            int choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1:
                    return Selection.STAFF;
                case 2:
                    return Selection.ADMIN;
                case 0:
                    return Selection.EXIT;
                default:
                    System.out.println("Invalid menu selection.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("PetStock");
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("1. Login as Staff");
        System.out.println("2. Login as Administrator");
        System.out.println("0. Exit");
    }

    public enum Selection {
        STAFF,
        ADMIN,
        EXIT
    }
}
