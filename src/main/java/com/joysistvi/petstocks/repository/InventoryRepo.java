package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.Inventory;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepo {
    List<Inventory> getAllInventory();
    List<Inventory> getAllInventory(String sortBy);
    Inventory getInventoryById(int id);
    List<Inventory> getInventoryByProductId(int productId);
    List<Inventory> searchInventory(String keyword);
    List<Inventory> getLowStockInventory(int maximumQuantity);
    List<Inventory> getExpiringInventory(LocalDate startDate, LocalDate endDate);
    List<Inventory> getInventoryByBatchCode(String batchCode);
    boolean createInventory(Inventory inventory);
    boolean updateInventory(Inventory inventory);
}
