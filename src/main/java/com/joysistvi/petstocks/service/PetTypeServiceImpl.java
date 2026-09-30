package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.PetType;
import com.joysistvi.petstocks.repository.PetTypeRepo;

import java.util.List;

public class PetTypeServiceImpl implements PetTypeService {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 250;

    private final PetTypeRepo petTypeRepo;

    public PetTypeServiceImpl(PetTypeRepo petTypeRepo) {
        this.petTypeRepo = petTypeRepo;
    }

    @Override
    public List<PetType> getAllPetTypes() {
        return getAllPetTypes("id");
    }

    @Override
    public List<PetType> getAllPetTypes(String sortBy) {
        return petTypeRepo.getAllPetTypes(getApprovedSortValue(sortBy));
    }

    @Override
    public PetType getPetTypeById(int id) {
        if (!isValidId(id, "find")) {
            return null;
        }

        PetType petType = petTypeRepo.getPetTypeById(id);
        if (petType == null) {
            System.out.println("Pet type not found.");
        }
        return petType;
    }

    @Override
    public List<PetType> searchPetTypes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return petTypeRepo.searchPetTypes(keyword.trim());
    }

    @Override
    public boolean createPetType(PetType petType) {
        if (!validatePetType(petType, false)) {
            return false;
        }

        normalizePetType(petType);
        return petTypeRepo.createPetType(petType);
    }

    @Override
    public boolean updatePetType(PetType petType) {
        if (!validatePetType(petType, true)) {
            return false;
        }

        normalizePetType(petType);
        return petTypeRepo.updatePetType(petType);
    }

    @Override
    public boolean archivePetType(int id) {
        return isValidId(id, "archive") && petTypeRepo.archivePetType(id);
    }

    @Override
    public boolean restorePetType(int id) {
        return isValidId(id, "restore") && petTypeRepo.restorePetType(id);
    }

    @Override
    public boolean deletePetType(int id) {
        return isValidId(id, "delete") && petTypeRepo.deletePetType(id);
    }

    @Override
    public List<PetType> getAllArchivedPetTypes() {
        return getAllArchivedPetTypes("id");
    }

    @Override
    public List<PetType> getAllArchivedPetTypes(String sortBy) {
        return petTypeRepo.getAllArchivedPetTypes(getApprovedSortValue(sortBy));
    }

    private String getApprovedSortValue(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean validatePetType(PetType petType, boolean requireId) {
        if (petType == null) {
            System.out.println("Pet type object cannot be null.");
            return false;
        }
        if (requireId && petType.getId() <= 0) {
            System.out.println("Invalid pet type ID for update.");
            return false;
        }
        if (petType.getName() == null || petType.getName().trim().isEmpty()) {
            System.out.println("Pet type name is required.");
            return false;
        }
        if (petType.getName().trim().length() > MAX_NAME_LENGTH) {
            System.out.println("Pet type name cannot exceed 100 characters.");
            return false;
        }
        if (petType.getDescription() != null
                && petType.getDescription().trim().length() > MAX_DESCRIPTION_LENGTH) {
            System.out.println("Pet type description cannot exceed 250 characters.");
            return false;
        }
        return true;
    }

    private void normalizePetType(PetType petType) {
        petType.setName(petType.getName().trim());

        if (petType.getDescription() != null) {
            String description = petType.getDescription().trim();
            petType.setDescription(description.isEmpty() ? null : description);
        }
    }

    private boolean isValidId(int id, String operation) {
        if (id <= 0) {
            System.out.println("Invalid pet type ID for " + operation + ".");
            return false;
        }
        return true;
    }
}
