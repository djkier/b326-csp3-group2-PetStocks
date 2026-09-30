package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.repository.CategoryRepo;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 250;

    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Category> getAllCategories() {
        return getAllCategories("id");
    }

    @Override
    public List<Category> getAllCategories(String sortBy) {
        return categoryRepo.getAllCategories(getApprovedSortValue(sortBy));
    }

    @Override
    public Category getCategoryById(int id) {
        if (!isValidId(id, "find")) {
            return null;
        }

        Category category = categoryRepo.getCategoryById(id);
        if (category == null) {
            System.out.println("Category not found.");
        }
        return category;
    }

    @Override
    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return categoryRepo.searchCategories(keyword.trim());
    }

    @Override
    public boolean createCategory(Category category) {
        if (!validateCategory(category, false)) {
            return false;
        }

        normalizeCategory(category);
        return categoryRepo.createCategory(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        if (!validateCategory(category, true)) {
            return false;
        }

        normalizeCategory(category);
        return categoryRepo.updateCategory(category);
    }

    @Override
    public boolean archiveCategory(int id) {
        return isValidId(id, "archive") && categoryRepo.archiveCategory(id);
    }

    @Override
    public boolean restoreCategory(int id) {
        return isValidId(id, "restore") && categoryRepo.restoreCategory(id);
    }

    @Override
    public boolean deleteCategory(int id) {
        return isValidId(id, "delete") && categoryRepo.deleteCategory(id);
    }

    @Override
    public List<Category> getAllArchivedCategories() {
        return getAllArchivedCategories("id");
    }

    @Override
    public List<Category> getAllArchivedCategories(String sortBy) {
        return categoryRepo.getAllArchivedCategories(getApprovedSortValue(sortBy));
    }

    private String getApprovedSortValue(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean validateCategory(Category category, boolean requireId) {
        if (category == null) {
            System.out.println("Category object cannot be null.");
            return false;
        }
        if (requireId && category.getId() <= 0) {
            System.out.println("Invalid category ID for update.");
            return false;
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            System.out.println("Category name is required.");
            return false;
        }
        if (category.getName().trim().length() > MAX_NAME_LENGTH) {
            System.out.println("Category name cannot exceed 100 characters.");
            return false;
        }
        if (category.getDescription() != null
                && category.getDescription().trim().length() > MAX_DESCRIPTION_LENGTH) {
            System.out.println("Category description cannot exceed 250 characters.");
            return false;
        }
        return true;
    }

    private void normalizeCategory(Category category) {
        category.setName(category.getName().trim());

        if (category.getDescription() != null) {
            String description = category.getDescription().trim();
            category.setDescription(description.isEmpty() ? null : description);
        }
    }

    private boolean isValidId(int id, String operation) {
        if (id <= 0) {
            System.out.println("Invalid category ID for " + operation + ".");
            return false;
        }
        return true;
    }
}
