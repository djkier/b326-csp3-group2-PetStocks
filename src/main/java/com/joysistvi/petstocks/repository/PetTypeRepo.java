package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.PetType;

import java.util.List;

public interface PetTypeRepo {
    List<PetType> getAllPetTypes();
    PetType getPetTypeById(int id);
    List<PetType> searchPetTypes(String keyword);
    boolean createPetType(PetType petType);
    boolean updatePetType(PetType petType);
    boolean archivePetType(int id);
    boolean restorePetType(int id);
    boolean deletePetType(int id);
    List<PetType> getAllArchivedPetTypes();
}
