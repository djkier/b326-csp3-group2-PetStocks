package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.Supplier;
import com.joysistvi.petstocks.service.SupplierService;

import java.util.List;

public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    public List<Supplier> handleViewAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    public List<Supplier> handleViewAllSuppliers(String sortBy) {
        return supplierService.getAllSuppliers(sortBy);
    }

    public Supplier handleGetSupplierById(int id) {
        return supplierService.getSupplierById(id);
    }

    public List<Supplier> searchSuppliers(String keyword) {
        return supplierService.searchSuppliers(keyword);
    }

    public boolean handleCreateSupplier(Supplier supplier) {
        return supplierService.createSupplier(supplier);
    }

    public boolean handleUpdateSupplier(Supplier supplier) {
        return supplierService.updateSupplier(supplier);
    }

    public boolean handleArchiveSupplier(int id) {
        return supplierService.archiveSupplier(id);
    }

    public boolean handleRestoreSupplier(int id) {
        return supplierService.restoreSupplier(id);
    }

    public boolean handleDeleteSupplier(int id) {
        return supplierService.deleteSupplier(id);
    }

    public List<Supplier> handleViewArchivedSuppliers() {
        return supplierService.getAllArchivedSuppliers();
    }

    public List<Supplier> handleViewArchivedSuppliers(String sortBy) {
        return supplierService.getAllArchivedSuppliers(sortBy);
    }
}
