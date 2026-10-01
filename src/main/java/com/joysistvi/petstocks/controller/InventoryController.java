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

    public List<Inventory> handleViewLowStockInventory() {
        return inventoryService.getLowStockInventory();
    }

    public List<Inventory> handleViewOutOfStockInventory() {
        return inventoryService.getOutOfStockInventory();
    }

    public List<Inventory> handleViewExpiringInventory() {
        return inventoryService.getExpiringInventory();
    }

    public List<Inventory> handleViewExpiredInventory() {
        return inventoryService.getExpiredInventory();
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
