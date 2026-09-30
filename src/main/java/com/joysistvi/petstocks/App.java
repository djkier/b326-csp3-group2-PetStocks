package com.joysistvi.petstocks;

import com.joysistvi.petstocks.cliview.PetTypeView;
import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.repository.PetTypeRepo;
import com.joysistvi.petstocks.repository.PetTypeRepoImpl;
import com.joysistvi.petstocks.service.PetTypeService;
import com.joysistvi.petstocks.service.PetTypeServiceImpl;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        DBConnection dbConnection = new DBConnection();
        PetTypeRepo petTypeRepo = new PetTypeRepoImpl(dbConnection);
        PetTypeService petTypeService = new PetTypeServiceImpl(petTypeRepo);
        PetTypeController petTypeController = new PetTypeController(petTypeService);

        try (Scanner scanner = new Scanner(System.in)) {
            PetTypeView petTypeView = new PetTypeView(petTypeController, scanner);
            petTypeView.run();
        }

    }
}
