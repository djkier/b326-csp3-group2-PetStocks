package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.Supplier;

import java.util.List;

public interface SupplierRepo {
    List<Supplier> getAllSuppliers();
    List<Supplier> getAllSuppliers(String sortBy);
    Supplier getSupplierById(int id);
    List<Supplier> searchSuppliers(String keyword);
    boolean createSupplier(Supplier supplier);
    boolean updateSupplier(Supplier supplier);
    boolean archiveSupplier(int id);
    boolean restoreSupplier(int id);
    boolean deleteSupplier(int id);
    List<Supplier> getAllArchivedSuppliers();
    List<Supplier> getAllArchivedSuppliers(String sortBy);
}
