package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.PetType;

import java.util.List;

public interface PetTypeService {
    List<PetType> getAllPetTypes();
    List<PetType> getAllPetTypes(String sortBy);
    PetType getPetTypeById(int id);
    List<PetType> searchPetTypes(String keyword);
    boolean createPetType(PetType petType);
    boolean updatePetType(PetType petType);
    boolean archivePetType(int id);
    boolean restorePetType(int id);
    boolean deletePetType(int id);
    List<PetType> getAllArchivedPetTypes();
    List<PetType> getAllArchivedPetTypes(String sortBy);
}
