package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Dispatch;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.repository.DispatchRepo;
import com.joysistvi.petstocks.repository.InventoryRepo;
import com.joysistvi.petstocks.repository.ProductRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DispatchServiceImpl implements DispatchService {
    private final DispatchRepo dispatchRepo;
    private final InventoryRepo inventoryRepo;
    private final ProductRepo productRepo;

    public DispatchServiceImpl(DispatchRepo dispatchRepo, InventoryRepo inventoryRepo,
                               ProductRepo productRepo) {
        this.dispatchRepo = dispatchRepo;
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<Dispatch> getDispatchHistory() {
        return getDispatchHistory("date");
    }

    @Override
    public List<Dispatch> getDispatchHistory(String sortBy) {
        return dispatchRepo.getAllDispatches(getApprovedSortValue(sortBy));
    }

    @Override
    public Dispatch getDispatchById(int id) {
        if (!isValidId(id, "dispatch")) {
            return null;
        }

        Dispatch dispatch = dispatchRepo.getDispatchById(id);
        if (dispatch == null) {
            System.out.println("Dispatch record not found.");
        }
        return dispatch;
    }

    @Override
    public List<Dispatch> getDispatchesByInventoryId(int inventoryId) {
        if (!isValidId(inventoryId, "inventory")) {
            return List.of();
        }
        if (inventoryRepo.getInventoryById(inventoryId) == null) {
            System.out.println("Inventory record not found.");
            return List.of();
        }
        return dispatchRepo.getDispatchesByInventoryId(inventoryId);
    }

    @Override
    public List<Dispatch> getDispatchesByProductId(int productId) {
        if (!isValidId(productId, "product")) {
            return List.of();
        }
        if (productRepo.getProductById(productId) == null) {
            System.out.println("Product not found.");
            return List.of();
        }
        return dispatchRepo.getDispatchesByProductId(productId);
    }

    @Override
    public List<Dispatch> getDispatchesByUserId(int userId) {
        if (!isValidId(userId, "user")) {
            return List.of();
        }
        if (dispatchRepo.getUsernameById(userId) == null) {
            System.out.println("User not found.");
            return List.of();
        }
        return dispatchRepo.getDispatchesByUserId(userId);
    }

    @Override
    public List<Dispatch> getDispatchesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            System.out.println("Start and end dates are required.");
            return List.of();
        }
        if (startDate.isAfter(endDate)) {
            System.out.println("Start date cannot be after the end date.");
            return List.of();
        }
        return dispatchRepo.getDispatchesByDateRange(startDate, endDate);
    }

    @Override
    public Map<Integer, String> getAvailableUsers() {
        return dispatchRepo.getAllUsers();
    }

    @Override
    public boolean recordStockOut(Dispatch dispatch) {
        if (!validateDispatch(dispatch)) {
            return false;
        }

        Inventory inventory = inventoryRepo.getInventoryById(dispatch.getInventory().getId());
        if (inventory == null) {
            System.out.println("Selected inventory record was not found.");
            return false;
        }
        if (inventory.getProduct().isArchived()) {
            System.out.println("Cannot dispatch stock for an archived product.");
            return false;
        }
        if (inventory.getProduct().getCategory().isArchived()) {
            System.out.println("Cannot dispatch stock from an archived product category.");
            return false;
        }
        if (inventory.getQuantity() < dispatch.getQuantityDispatched()) {
            System.out.println("Insufficient inventory quantity. Available: " +
                    inventory.getQuantity());
            return false;
        }

        String username = dispatchRepo.getUsernameById(dispatch.getUserId());
        if (username == null) {
            System.out.println("Selected user was not found.");
            return false;
        }

        dispatch.setInventory(inventory);
        dispatch.setUsername(username);
        if (dispatch.getDatetimeDispatched() == null) {
            dispatch.setDatetimeDispatched(LocalDateTime.now());
        }

        boolean isRecorded = dispatchRepo.recordStockOut(dispatch);
        if (isRecorded) {
            inventory.setQuantity(inventory.getQuantity() - dispatch.getQuantityDispatched());
        }
        return isRecorded;
    }

    private boolean validateDispatch(Dispatch dispatch) {
        if (dispatch == null) {
            System.out.println("Dispatch object cannot be null.");
            return false;
        }
        if (dispatch.getInventory() == null || dispatch.getInventory().getId() <= 0) {
            System.out.println("A valid inventory record is required.");
            return false;
        }
        if (dispatch.getUserId() <= 0) {
            System.out.println("A valid user is required.");
            return false;
        }
        if (dispatch.getQuantityDispatched() <= 0) {
            System.out.println("Dispatched quantity must be greater than zero.");
            return false;
        }
        if (dispatch.getDatetimeDispatched() != null
                && dispatch.getDatetimeDispatched().isAfter(LocalDateTime.now())) {
            System.out.println("Dispatch date and time cannot be in the future.");
            return false;
        }
        return true;
    }

    private String getApprovedSortValue(String sortBy) {
        return switch (sortBy) {
            case "date", "product", "user" -> sortBy;
            default -> "id";
        };
    }

    private boolean isValidId(int id, String entityName) {
        if (id <= 0) {
            System.out.println("Invalid " + entityName + " ID.");
            return false;
        }
        return true;
    }
}
