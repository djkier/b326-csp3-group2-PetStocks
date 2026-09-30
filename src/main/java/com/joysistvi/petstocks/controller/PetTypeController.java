package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.PetType;
import com.joysistvi.petstocks.service.PetTypeService;

import java.util.List;

public class PetTypeController {
    private final PetTypeService petTypeService;

    public PetTypeController(PetTypeService petTypeService) {
        this.petTypeService = petTypeService;
    }

    public List<PetType> handleViewAllPetTypes() {
        return petTypeService.getAllPetTypes();
    }

    public PetType handleGetPetTypeById(int id) {
        return petTypeService.getPetTypeById(id);
    }

    public List<PetType> searchPetTypes(String keyword) {
        return petTypeService.searchPetTypes(keyword);
    }

    public boolean handleCreatePetType(PetType petType) {
        return petTypeService.createPetType(petType);
    }

    public boolean handleUpdatePetType(PetType petType) {
        return petTypeService.updatePetType(petType);
    }

    public boolean handleArchivePetType(int id) {
        return petTypeService.archivePetType(id);
    }

    public boolean handleRestorePetType(int id) {
        return petTypeService.restorePetType(id);
    }

    public boolean handleDeletePetType(int id) {
        return petTypeService.deletePetType(id);
    }

    public List<PetType> handleViewArchivedPetTypes() {
        return petTypeService.getAllArchivedPetTypes();
    }
}
