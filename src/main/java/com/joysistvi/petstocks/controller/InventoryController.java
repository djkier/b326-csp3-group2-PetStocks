package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.service.InventoryService;

import java.util.List;

public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public List<Inventory> handleViewAllInventory() {
        return inventoryService.getAllInventory();
    }

    public List<Inventory> handleViewAllInventory(String sortBy) {
        return inventoryService.getAllInventory(sortBy);
    }

    public Inventory handleGetInventoryById(int id) {
        return inventoryService.getInventoryById(id);
    }

    public List<Inventory> handleViewInventoryByProductId(int productId) {
        return inventoryService.getInventoryByProductId(productId);
    }

    public List<Inventory> searchInventory(String keyword) {
        return inventoryService.searchInventory(keyword);
    }

    public List<Inventory> handleViewLowStockInventory(int maximumQuantity) {
        return inventoryService.getLowStockInventory(maximumQuantity);
    }

    public List<Inventory> handleViewExpiringInventory(int daysAhead) {
        return inventoryService.getExpiringInventory(daysAhead);
    }

    public List<Inventory> handleViewInventoryByBatchCode(String batchCode) {
        return inventoryService.getInventoryByBatchCode(batchCode);
    }

    public boolean handleCreateInventory(Inventory inventory) {
        return inventoryService.createInventory(inventory);
    }

    public boolean handleUpdateInventory(Inventory inventory) {
        return inventoryService.updateInventory(inventory);
    }
}
