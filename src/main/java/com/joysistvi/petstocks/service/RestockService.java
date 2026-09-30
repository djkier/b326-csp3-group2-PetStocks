package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Restock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RestockService {
    List<Restock> getRestockHistory();
    List<Restock> getRestockHistory(String sortBy);
    Restock getRestockById(int id);
    List<Restock> getRestocksByInventoryId(int inventoryId);
    List<Restock> getRestocksBySupplierId(int supplierId);
    List<Restock> getRestocksByUserId(int userId);
    List<Restock> getRestocksByDateRange(LocalDate startDate, LocalDate endDate);
    Map<Integer, String> getAvailableUsers();
    boolean recordStockIn(Restock restock);
}
