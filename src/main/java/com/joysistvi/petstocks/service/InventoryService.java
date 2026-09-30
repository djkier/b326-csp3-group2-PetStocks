package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Inventory;

import java.util.List;

public interface InventoryService {
    List<Inventory> getAllInventory();
    List<Inventory> getAllInventory(String sortBy);
    Inventory getInventoryById(int id);
    List<Inventory> getInventoryByProductId(int productId);
    List<Inventory> searchInventory(String keyword);
    List<Inventory> getLowStockInventory(int maximumQuantity);
    List<Inventory> getExpiringInventory(int daysAhead);
    List<Inventory> getInventoryByBatchCode(String batchCode);
    boolean createInventory(Inventory inventory);
    boolean updateInventory(Inventory inventory);
}
