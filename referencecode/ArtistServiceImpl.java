package com.joysistvi.recording.service;

import com.joysistvi.recording.model.Artist;
import com.joysistvi.recording.repository.ArtistRepo;

import java.util.List;

public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepo artistRepo; // Composition

    // Constructor injection
    public ArtistServiceImpl(ArtistRepo artistRepo) {
        this.artistRepo = artistRepo;
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistRepo.getAllArtists();
    }

    @Override
    public Artist getArtistById(int id) {
        if (id <= 0) {
            System.out.println("Invalid artist ID.");
            return null;
        }

        Artist artist = artistRepo.getArtistById(id);
        if (artist == null) {
            System.out.println("Artist not found.");
        }
        return artist;
    }

    @Override
    public List<Artist> searchArtist(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return artistRepo.searchArtist(keyword.trim());
    }

    @Override
    public boolean createArtist(Artist artist) {
        if (artist == null) {
            System.out.println("Artist object cannot be null.");
            return false;
        }

        if (artist.getName() == null || artist.getName().trim().isEmpty()) {
            System.out.println("Artist name is required.");
            return false;
        }


        artist.setName(artist.getName().trim());
        return artistRepo.createArtist(artist);
    }

    @Override
    public boolean updateArtist(Artist artist) {
        if (artist == null || artist.getId() <= 0) {
            System.out.println("Invalid artist data for update.");
            return false;
        }

        if (artist.getName() == null || artist.getName().trim().isEmpty()) {
            System.out.println("Artist name cannot be empty.");
            return false;
        }

        artist.setName(artist.getName().trim());
        return artistRepo.updateArtist(artist);
    }

    @Override
    public boolean archiveArtist(int id) {
        if (id <= 0) {
            System.out.println("Invalid artist ID for archive.");
            return false;
        }

        return artistRepo.archiveArtist(id);
    }

    @Override
    public boolean restoreArtist(int id) {
        if (id <= 0) {
            System.out.println("Invalid artist ID for restore.");
            return false;
        }

        return artistRepo.restoreArtist(id);
    }

    @Override
    public boolean deleteArtist(int id) {
        if (id <= 0) {
            System.out.println("Invalid artist ID for deletion.");
            return false;
        }

        return artistRepo.deleteArtist(id);
    }

    @Override
    public List<Artist> getAllArchivedArtists() {
        return artistRepo.getAllArchivedArtists();
    }
}
