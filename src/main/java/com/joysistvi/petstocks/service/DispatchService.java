package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Dispatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DispatchService {
    List<Dispatch> getDispatchHistory();
    List<Dispatch> getDispatchHistory(String sortBy);
    Dispatch getDispatchById(int id);
    List<Dispatch> getDispatchesByInventoryId(int inventoryId);
    List<Dispatch> getDispatchesByProductId(int productId);
    List<Dispatch> getDispatchesByUserId(int userId);
    List<Dispatch> getDispatchesByDateRange(LocalDate startDate, LocalDate endDate);
    Map<Integer, String> getAvailableUsers();
    boolean recordStockOut(Dispatch dispatch);
}
