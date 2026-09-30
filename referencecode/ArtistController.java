package com.joysistvi.recording.controller;

import com.joysistvi.recording.model.Artist;
import com.joysistvi.recording.service.ArtistService;

import java.util.List;

public class ArtistController {
    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    // READ
    public List<Artist> handleViewAllArtists() {
        return artistService.getAllArtists();
    }

    public Artist handleGetArtistById(int id) {
        return artistService.getArtistById(id);
    }

    public List<Artist> searchArtist(String keyword) {
        return artistService.searchArtist(keyword);
    }

    public List<Artist> handleViewArchivedArtists() {
        return artistService.getAllArchivedArtists();
    }

    // CREATE
    public boolean handleCreateArtist(Artist artist) {
        return artistService.createArtist(artist);
    }

    // UPDATE
    public boolean handleUpdateArtist(Artist artist) {
        return artistService.updateArtist(artist);
    }

    // ARCHIVE / RESTORE
    public boolean handleArchiveArtist(int id) {
        return artistService.archiveArtist(id);
    }

    public boolean handleRestoreArtist(int id) {
        return artistService.restoreArtist(id);
    }

    // DELETE
    public boolean handleDeleteArtist(int id) {
        return artistService.deleteArtist(id);
    }
}
