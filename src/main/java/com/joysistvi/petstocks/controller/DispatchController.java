package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.Dispatch;
import com.joysistvi.petstocks.service.DispatchService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DispatchController {
    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    public List<Dispatch> handleViewDispatchHistory() {
        return dispatchService.getDispatchHistory();
    }

    public List<Dispatch> handleViewDispatchHistory(String sortBy) {
        return dispatchService.getDispatchHistory(sortBy);
    }

    public Dispatch handleFindDispatchById(int id) {
        return dispatchService.getDispatchById(id);
    }

    public List<Dispatch> handleViewDispatchesByInventory(int inventoryId) {
        return dispatchService.getDispatchesByInventoryId(inventoryId);
    }

    public List<Dispatch> handleViewDispatchesByProduct(int productId) {
        return dispatchService.getDispatchesByProductId(productId);
    }

    public List<Dispatch> handleViewDispatchesByUser(int userId) {
        return dispatchService.getDispatchesByUserId(userId);
    }

    public List<Dispatch> handleViewDispatchesByDateRange(LocalDate startDate,
                                                           LocalDate endDate) {
        return dispatchService.getDispatchesByDateRange(startDate, endDate);
    }

    public Map<Integer, String> handleGetAvailableUsers() {
        return dispatchService.getAvailableUsers();
    }

    public boolean handleRecordStockOut(Dispatch dispatch) {
        return dispatchService.recordStockOut(dispatch);
    }
}
