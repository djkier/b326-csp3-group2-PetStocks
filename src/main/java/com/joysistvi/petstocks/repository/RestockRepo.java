package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.Restock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RestockRepo {
    List<Restock> getAllRestocks();
    List<Restock> getAllRestocks(String sortBy);
    Restock getRestockById(int id);
    List<Restock> getRestocksByInventoryId(int inventoryId);
    List<Restock> getRestocksBySupplierId(int supplierId);
    List<Restock> getRestocksByUserId(int userId);
    List<Restock> getRestocksByDateRange(LocalDate startDate, LocalDate endDate);
    Map<Integer, String> getAllUsers();
    String getUsernameById(int userId);
    boolean recordStockIn(Restock restock);
}
