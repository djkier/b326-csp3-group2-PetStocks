package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Supplier;
import com.joysistvi.petstocks.repository.SupplierRepo;

import java.util.List;
import java.util.regex.Pattern;

public class SupplierServiceImpl implements SupplierService {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ADDRESS_LENGTH = 200;
    private static final int MAX_CONTACT_NUMBER_LENGTH = 20;
    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MIN_PHONE_DIGITS = 7;
    private static final int MAX_PHONE_DIGITS = 15;

    private static final Pattern CONTACT_NUMBER_PATTERN =
            Pattern.compile("^\\+?[0-9()\\-\\s]+$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final SupplierRepo supplierRepo;

    public SupplierServiceImpl(SupplierRepo supplierRepo) {
        this.supplierRepo = supplierRepo;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return getAllSuppliers("id");
    }

    @Override
    public List<Supplier> getAllSuppliers(String sortBy) {
        return supplierRepo.getAllSuppliers(getApprovedSortValue(sortBy));
    }

    @Override
    public Supplier getSupplierById(int id) {
        if (!isValidId(id, "find")) {
            return null;
        }

        Supplier supplier = supplierRepo.getSupplierById(id);
        if (supplier == null) {
            System.out.println("Supplier not found.");
        }
        return supplier;
    }

    @Override
    public List<Supplier> searchSuppliers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return supplierRepo.searchSuppliers(keyword.trim());
    }

    @Override
    public boolean createSupplier(Supplier supplier) {
        if (!validateSupplier(supplier, false)) {
            return false;
        }

        normalizeSupplier(supplier);
        return supplierRepo.createSupplier(supplier);
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        if (!validateSupplier(supplier, true)) {
            return false;
        }

        normalizeSupplier(supplier);
        return supplierRepo.updateSupplier(supplier);
    }

    @Override
    public boolean archiveSupplier(int id) {
        return isValidId(id, "archive") && supplierRepo.archiveSupplier(id);
    }

    @Override
    public boolean restoreSupplier(int id) {
        return isValidId(id, "restore") && supplierRepo.restoreSupplier(id);
    }

    @Override
    public boolean deleteSupplier(int id) {
        return isValidId(id, "delete") && supplierRepo.deleteSupplier(id);
    }

    @Override
    public List<Supplier> getAllArchivedSuppliers() {
        return getAllArchivedSuppliers("id");
    }

    @Override
    public List<Supplier> getAllArchivedSuppliers(String sortBy) {
        return supplierRepo.getAllArchivedSuppliers(getApprovedSortValue(sortBy));
    }

    private String getApprovedSortValue(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean validateSupplier(Supplier supplier, boolean requireId) {
        if (supplier == null) {
            System.out.println("Supplier object cannot be null.");
            return false;
        }
        if (requireId && supplier.getId() <= 0) {
            System.out.println("Invalid supplier ID for update.");
            return false;
        }
        if (!validateRequiredField(supplier.getName(), "Supplier name", MAX_NAME_LENGTH)) {
            return false;
        }
        if (!validateRequiredField(supplier.getAddress(), "Supplier address", MAX_ADDRESS_LENGTH)) {
            return false;
        }
        if (!validateContactNumber(supplier.getContactNumber())) {
            return false;
        }
        return validateEmail(supplier.getEmail());
    }

    private boolean validateRequiredField(String value, String fieldName, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println(fieldName + " is required.");
            return false;
        }
        if (value.trim().length() > maxLength) {
            System.out.println(fieldName + " cannot exceed " + maxLength + " characters.");
            return false;
        }
        return true;
    }

    private boolean validateContactNumber(String contactNumber) {
        if (!validateRequiredField(contactNumber, "Supplier contact number",
                MAX_CONTACT_NUMBER_LENGTH)) {
            return false;
        }

        String trimmedContactNumber = contactNumber.trim();
        int digitCount = trimmedContactNumber.replaceAll("\\D", "").length();
        if (!CONTACT_NUMBER_PATTERN.matcher(trimmedContactNumber).matches()
                || digitCount < MIN_PHONE_DIGITS || digitCount > MAX_PHONE_DIGITS) {
            System.out.println("Supplier contact number must contain 7 to 15 digits and may " +
                    "include spaces, parentheses, hyphens, or a leading plus sign.");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (!validateRequiredField(email, "Supplier email", MAX_EMAIL_LENGTH)) {
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            System.out.println("Supplier email must be a valid email address.");
            return false;
        }
        return true;
    }

    private void normalizeSupplier(Supplier supplier) {
        supplier.setName(supplier.getName().trim());
        supplier.setAddress(supplier.getAddress().trim());
        supplier.setContactNumber(supplier.getContactNumber().trim());
        supplier.setEmail(supplier.getEmail().trim());
    }

    private boolean isValidId(int id, String operation) {
        if (id <= 0) {
            System.out.println("Invalid supplier ID for " + operation + ".");
            return false;
        }
        return true;
    }
}
