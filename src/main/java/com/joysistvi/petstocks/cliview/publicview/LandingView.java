package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class LandingView {
    private final Scanner scanner;

    public LandingView(Scanner scanner) {
        this.scanner = scanner;
    }

    public Selection promptForSelection() {
        while (true) {
            printMenu();
            int choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1:
                    return Selection.LOGIN;
                case 2:
                    return Selection.REGISTER;
                case 0:
                    return Selection.EXIT;
                default:
                    System.out.println("Invalid menu selection.");
            }
        }
    }

    private void printMenu() {
        CliViewUtility.showHeader("PetStock");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
    }

    public enum Selection {
        LOGIN,
        REGISTER,
        EXIT
    }
}
