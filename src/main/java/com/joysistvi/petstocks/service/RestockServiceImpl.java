package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Restock;
import com.joysistvi.petstocks.model.Supplier;
import com.joysistvi.petstocks.repository.InventoryRepo;
import com.joysistvi.petstocks.repository.RestockRepo;
import com.joysistvi.petstocks.repository.SupplierRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RestockServiceImpl implements RestockService {
    private final RestockRepo restockRepo;
    private final InventoryRepo inventoryRepo;
    private final SupplierRepo supplierRepo;

    public RestockServiceImpl(RestockRepo restockRepo, InventoryRepo inventoryRepo,
                              SupplierRepo supplierRepo) {
        this.restockRepo = restockRepo;
        this.inventoryRepo = inventoryRepo;
        this.supplierRepo = supplierRepo;
    }

    @Override
    public List<Restock> getRestockHistory() {
        return getRestockHistory("date");
    }

    @Override
    public List<Restock> getRestockHistory(String sortBy) {
        return restockRepo.getAllRestocks(getApprovedSortValue(sortBy));
    }

    @Override
    public Restock getRestockById(int id) {
        if (!isValidId(id, "restock")) {
            return null;
        }

        Restock restock = restockRepo.getRestockById(id);
        if (restock == null) {
            System.out.println("Restock record not found.");
        }
        return restock;
    }

    @Override
    public List<Restock> getRestocksByInventoryId(int inventoryId) {
        if (!isValidId(inventoryId, "inventory")) {
            return List.of();
        }
        if (inventoryRepo.getInventoryById(inventoryId) == null) {
            System.out.println("Inventory record not found.");
            return List.of();
        }
        return restockRepo.getRestocksByInventoryId(inventoryId);
    }

    @Override
    public List<Restock> getRestocksBySupplierId(int supplierId) {
        if (!isValidId(supplierId, "supplier")) {
            return List.of();
        }
        if (supplierRepo.getSupplierById(supplierId) == null) {
            System.out.println("Supplier not found.");
            return List.of();
        }
        return restockRepo.getRestocksBySupplierId(supplierId);
    }

    @Override
    public List<Restock> getRestocksByUserId(int userId) {
        if (!isValidId(userId, "user")) {
            return List.of();
        }
        if (restockRepo.getUsernameById(userId) == null) {
            System.out.println("User not found.");
            return List.of();
        }
        return restockRepo.getRestocksByUserId(userId);
    }

    @Override
    public List<Restock> getRestocksByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            System.out.println("Start and end dates are required.");
            return List.of();
        }
        if (startDate.isAfter(endDate)) {
            System.out.println("Start date cannot be after the end date.");
            return List.of();
        }
        return restockRepo.getRestocksByDateRange(startDate, endDate);
    }

    @Override
    public Map<Integer, String> getAvailableUsers() {
        return restockRepo.getAllUsers();
    }

    @Override
    public boolean recordStockIn(Restock restock) {
        if (!validateRestock(restock)) {
            return false;
        }

        Inventory inventory = inventoryRepo.getInventoryById(restock.getInventory().getId());
        if (inventory == null) {
            System.out.println("Selected inventory record was not found.");
            return false;
        }
        if (inventory.getProduct().isArchived()) {
            System.out.println("Cannot restock an inventory record for an archived product.");
            return false;
        }

        Supplier supplier = supplierRepo.getSupplierById(restock.getSupplier().getId());
        if (supplier == null) {
            System.out.println("Selected supplier was not found.");
            return false;
        }
        if (supplier.isArchived()) {
            System.out.println("Cannot record stock from an archived supplier.");
            return false;
        }

        String username = restockRepo.getUsernameById(restock.getUserId());
        if (username == null) {
            System.out.println("Selected user was not found.");
            return false;
        }

        if ((long) inventory.getQuantity() + restock.getQuantityDelivered()
                > Integer.MAX_VALUE) {
            System.out.println("The delivered quantity would exceed the inventory limit.");
            return false;
        }

        restock.setInventory(inventory);
        restock.setSupplier(supplier);
        restock.setUsername(username);
        if (restock.getDatetimeDelivered() == null) {
            restock.setDatetimeDelivered(LocalDateTime.now());
        }

        boolean isRecorded = restockRepo.recordStockIn(restock);
        if (isRecorded) {
            inventory.setQuantity(inventory.getQuantity() + restock.getQuantityDelivered());
        }
        return isRecorded;
    }

    private boolean validateRestock(Restock restock) {
        if (restock == null) {
            System.out.println("Restock object cannot be null.");
            return false;
        }
        if (restock.getInventory() == null || restock.getInventory().getId() <= 0) {
            System.out.println("A valid inventory record is required.");
            return false;
        }
        if (restock.getSupplier() == null || restock.getSupplier().getId() <= 0) {
            System.out.println("A valid supplier is required.");
            return false;
        }
        if (restock.getUserId() <= 0) {
            System.out.println("A valid user is required.");
            return false;
        }
        if (restock.getQuantityDelivered() <= 0) {
            System.out.println("Delivered quantity must be greater than zero.");
            return false;
        }
        if (restock.getDatetimeDelivered() != null
                && restock.getDatetimeDelivered().isAfter(LocalDateTime.now())) {
            System.out.println("Delivery date and time cannot be in the future.");
            return false;
        }
        return true;
    }

    private String getApprovedSortValue(String sortBy) {
        return switch (sortBy) {
            case "date", "product", "supplier", "user" -> sortBy;
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
