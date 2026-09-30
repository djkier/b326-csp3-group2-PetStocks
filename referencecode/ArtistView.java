package com.joysistvi.petstocks.referencecode;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.Utility.CliViewUtility;
import com.joysistvi.recording.controller.ArtistController;
import com.joysistvi.recording.model.Artist;

import java.util.List;
import java.util.Scanner;

public class ArtistView {

    private final ArtistController artistController;
    private final Scanner scanner;

    public ArtistView(ArtistController artistController, Scanner scanner) {
        this.artistController = artistController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();

            choice = promptChoice();

            switch (choice) {
                case 1 -> viewAllArtists();
                case 2 -> searchArtist();
                case 3 -> addArtists();
                case 4 -> updateArtist();
                case 5 -> archiveArtist();
                case 6 -> restoreArtist();
                case 7 -> deleteArtist();
                case 8 -> viewAllArchivedArtists();

                case 0 -> System.out.println("Returning to main menu...");
                default -> InputUtility.displayError("Invalid menu selection.");
            }

            if (choice != 0) {
                InputUtility.pressEnterToContinue(scanner);
            }
        } while (choice != 0);
    }


    private void printMenu() {
        CliViewUtility.showHeader("Artist Management");
        System.out.println("1. View All Artists");
        System.out.println("2. Search Artist");
        System.out.println("3. Add Artist");
        System.out.println("4. Update Artist");
        System.out.println("5. Archive Artist");
        System.out.println("6. Restore Artist");
        System.out.println("7. Delete Artist");
        System.out.println("8. View All Archived Artists");
        System.out.println("0. Back");
    }

    public int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    private void viewAllArtists() {
        CliViewUtility.showHeader("View All Artists");
        List<Artist> artists = artistController.handleViewAllArtists();
        printArtists(artists);
    }

    private void searchArtist() {
        CliViewUtility.showHeader("Search Artists");
        System.out.print("Enter name: ");
        String keyword = scanner.nextLine();
        List<Artist> artists = artistController.searchArtist(keyword);
        printArtists(artists);
    }

    private void addArtists() {
        CliViewUtility.showHeader("Add Artist");
        System.out.print("Name: ");
        String name = scanner.nextLine();

        Artist artist = new Artist(name);

        boolean isSuccess = artistController.handleCreateArtist(artist);
        System.out.println(isSuccess ? "Artist added successfully." : "Failed to add artist.");

        if (isSuccess) {
            System.out.println();
            viewAllArtists(); // read-after-write
        }
    }

    public void updateArtist() {
        CliViewUtility.showHeader("Update Artist");

        // Show all artists first so the admin can see which ID to pick
        printArtists(artistController.handleViewAllArtists());

        System.out.print("Arists ID to update: ");
        int id = InputUtility.readInt(scanner);

        Artist current = artistController.handleGetArtistById(id);

        if (current == null) {
            System.out.println("No artist found with ID " + id + ". Please check the ID and try again.");
            return;
        }

        System.out.println("New Name [ " + current.getName() + " ] (press Enter to keep the current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = current.getName();
        }

        Artist artist = new Artist(id, name);

        boolean isSuccess = artistController.handleUpdateArtist(artist);
        System.out.println(isSuccess ? "Artist updated successfully." : "Failed to update artist.");

        if (isSuccess) {
            System.out.println();
            viewAllArtists(); // read-after-write / refresh-after-mutation
        }
    }

    private void archiveArtist() {
        CliViewUtility.showHeader("Archive Artist");

        printArtists(artistController.handleViewAllArtists());

        System.out.print("Artist ID to archive: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = artistController.handleArchiveArtist(id);
        System.out.println(isSuccess ? "Artist archived successfully." : "Failed to archive artist.");

        if (isSuccess) {
            System.out.println();
            viewAllArtists();
        }
    }

    private void restoreArtist() {
        CliViewUtility.showHeader("Restore Artist");

        printArtists(artistController.handleViewArchivedArtists());

        System.out.print("Artist ID to restore: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = artistController.handleRestoreArtist(id);
        System.out.println(isSuccess ? "Artist restored successfully." : "Failed to restore artist.");

        if (isSuccess) {
            System.out.println();
            viewAllArchivedArtists();
        }
    }

    private void deleteArtist() {
        CliViewUtility.showHeader("Delete Artist");

        printArtists(artistController.handleViewArchivedArtists());

        System.out.print("Artist ID to delete: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = artistController.handleDeleteArtist(id);
        System.out.println(isSuccess ? "Artist deleted successfully." : "Failed to delete artist.");

        if (isSuccess) {
            System.out.println();
            viewAllArchivedArtists();
        }
    }

    private void viewAllArchivedArtists() {
        CliViewUtility.showHeader("Archived Artists");
        List<Artist> artists = artistController.handleViewArchivedArtists();
        printArtists(artists);
    }


    public void printArtists(List<Artist> artists) {
        if (artists.isEmpty()) {
            System.out.println("No artists found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+"  + "-".repeat(27) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s |%n", "ID", "Name");
        System.out.println(border);

        for (Artist artist : artists) {
            System.out.printf("| %-4s | %-25s |%n", artist.getId(), artist.getName());
        }

        System.out.println(border);
    }
}
