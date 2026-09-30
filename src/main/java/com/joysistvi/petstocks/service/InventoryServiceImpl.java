package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.repository.InventoryRepo;
import com.joysistvi.petstocks.repository.ProductRepo;

import java.time.LocalDate;
import java.util.List;

public class InventoryServiceImpl implements InventoryService {
    private static final int MAX_BATCH_CODE_LENGTH = 100;
    private static final int MAX_REMARK_LENGTH = 250;

    private final InventoryRepo inventoryRepo;
    private final ProductRepo productRepo;

    public InventoryServiceImpl(InventoryRepo inventoryRepo, ProductRepo productRepo) {
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<Inventory> getAllInventory() {
        return getAllInventory("id");
    }

    @Override
    public List<Inventory> getAllInventory(String sortBy) {
        return inventoryRepo.getAllInventory(getApprovedSortValue(sortBy));
    }

    @Override
    public Inventory getInventoryById(int id) {
        if (!isValidId(id, "inventory")) {
            return null;
        }

        Inventory inventory = inventoryRepo.getInventoryById(id);
        if (inventory == null) {
            System.out.println("Inventory record not found.");
        }
        return inventory;
    }

    @Override
    public List<Inventory> getInventoryByProductId(int productId) {
        if (!isValidId(productId, "product")) {
            return List.of();
        }
        if (productRepo.getProductById(productId) == null) {
            System.out.println("Product not found.");
            return List.of();
        }

        return inventoryRepo.getInventoryByProductId(productId);
    }

    @Override
    public List<Inventory> searchInventory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return inventoryRepo.searchInventory(keyword.trim());
    }

    @Override
    public List<Inventory> getLowStockInventory(int maximumQuantity) {
        if (maximumQuantity < 0) {
            System.out.println("Low-stock quantity cannot be negative.");
            return List.of();
        }

        return inventoryRepo.getLowStockInventory(maximumQuantity);
    }

    @Override
    public List<Inventory> getExpiringInventory(int daysAhead) {
        if (daysAhead < 0) {
            System.out.println("Expiration window cannot be negative.");
            return List.of();
        }

        LocalDate startDate = LocalDate.now();
        return inventoryRepo.getExpiringInventory(startDate, startDate.plusDays(daysAhead));
    }

    @Override
    public List<Inventory> getInventoryByBatchCode(String batchCode) {
        if (batchCode == null || batchCode.trim().isEmpty()) {
            System.out.println("Batch code cannot be empty.");
            return List.of();
        }
        if (batchCode.trim().length() > MAX_BATCH_CODE_LENGTH) {
            System.out.println("Batch code cannot exceed 100 characters.");
            return List.of();
        }

        return inventoryRepo.getInventoryByBatchCode(batchCode.trim());
    }

    @Override
    public boolean createInventory(Inventory inventory) {
        if (!validateInventory(inventory, false) || !assignActiveProduct(inventory)) {
            return false;
        }

        normalizeInventory(inventory);
        return inventoryRepo.createInventory(inventory);
    }

    @Override
    public boolean updateInventory(Inventory inventory) {
        if (!validateInventory(inventory, true) || !assignActiveProduct(inventory)) {
            return false;
        }

        normalizeInventory(inventory);
        return inventoryRepo.updateInventory(inventory);
    }

    private String getApprovedSortValue(String sortBy) {
        return switch (sortBy) {
            case "product", "quantity", "expiration" -> sortBy;
            default -> "id";
        };
    }

    private boolean validateInventory(Inventory inventory, boolean requireId) {
        if (inventory == null) {
            System.out.println("Inventory object cannot be null.");
            return false;
        }
        if (requireId && inventory.getId() <= 0) {
            System.out.println("Invalid inventory ID for update.");
            return false;
        }
        if (inventory.getProduct() == null || inventory.getProduct().getId() <= 0) {
            System.out.println("A valid product is required.");
            return false;
        }
        if (inventory.getQuantity() < 0) {
            System.out.println("Inventory quantity cannot be negative.");
            return false;
        }
        if (inventory.getExpiration() == null) {
            System.out.println("Inventory expiration date is required.");
            return false;
        }
        if (inventory.getBatchCode() == null || inventory.getBatchCode().trim().isEmpty()) {
            System.out.println("Batch code is required.");
            return false;
        }
        if (inventory.getBatchCode().trim().length() > MAX_BATCH_CODE_LENGTH) {
            System.out.println("Batch code cannot exceed 100 characters.");
            return false;
        }
        if (inventory.getRemark() != null
                && inventory.getRemark().trim().length() > MAX_REMARK_LENGTH) {
            System.out.println("Inventory remark cannot exceed 250 characters.");
            return false;
        }
        return true;
    }

    private boolean assignActiveProduct(Inventory inventory) {
        Product product = productRepo.getProductById(inventory.getProduct().getId());
        if (product == null) {
            System.out.println("Selected product was not found.");
            return false;
        }
        if (product.isArchived()) {
            System.out.println("Selected product is archived.");
            return false;
        }
        if (product.getCategory().isArchived()) {
            System.out.println("Selected product belongs to an archived category.");
            return false;
        }

        inventory.setProduct(product);
        return true;
    }

    private void normalizeInventory(Inventory inventory) {
        inventory.setBatchCode(inventory.getBatchCode().trim());

        if (inventory.getRemark() != null) {
            String remark = inventory.getRemark().trim();
            inventory.setRemark(remark.isEmpty() ? null : remark);
        }
    }

    private boolean isValidId(int id, String entityName) {
        if (id <= 0) {
            System.out.println("Invalid " + entityName + " ID.");
            return false;
        }
        return true;
    }
}
