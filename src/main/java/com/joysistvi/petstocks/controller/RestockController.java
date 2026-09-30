package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.Restock;
import com.joysistvi.petstocks.service.RestockService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RestockController {
    private final RestockService restockService;

    public RestockController(RestockService restockService) {
        this.restockService = restockService;
    }

    public List<Restock> handleViewRestockHistory() {
        return restockService.getRestockHistory();
    }

    public List<Restock> handleViewRestockHistory(String sortBy) {
        return restockService.getRestockHistory(sortBy);
    }

    public Restock handleFindRestockById(int id) {
        return restockService.getRestockById(id);
    }

    public List<Restock> handleViewRestocksByInventory(int inventoryId) {
        return restockService.getRestocksByInventoryId(inventoryId);
    }

    public List<Restock> handleViewRestocksBySupplier(int supplierId) {
        return restockService.getRestocksBySupplierId(supplierId);
    }

    public List<Restock> handleViewRestocksByUser(int userId) {
        return restockService.getRestocksByUserId(userId);
    }

    public List<Restock> handleViewRestocksByDateRange(LocalDate startDate,
                                                        LocalDate endDate) {
        return restockService.getRestocksByDateRange(startDate, endDate);
    }

    public Map<Integer, String> handleGetAvailableUsers() {
        return restockService.getAvailableUsers();
    }

    public boolean handleRecordStockIn(Restock restock) {
        return restockService.recordStockIn(restock);
    }
}
