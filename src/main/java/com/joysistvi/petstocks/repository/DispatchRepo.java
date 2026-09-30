package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.Dispatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DispatchRepo {
    List<Dispatch> getAllDispatches();
    List<Dispatch> getAllDispatches(String sortBy);
    Dispatch getDispatchById(int id);
    List<Dispatch> getDispatchesByInventoryId(int inventoryId);
    List<Dispatch> getDispatchesByProductId(int productId);
    List<Dispatch> getDispatchesByUserId(int userId);
    List<Dispatch> getDispatchesByDateRange(LocalDate startDate, LocalDate endDate);
    Map<Integer, String> getAllUsers();
    String getUsernameById(int userId);
    boolean recordStockOut(Dispatch dispatch);
}
