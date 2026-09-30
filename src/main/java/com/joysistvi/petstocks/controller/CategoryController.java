package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.service.CategoryService;

import java.util.List;

public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<Category> handleViewAllCategories() {
        return categoryService.getAllCategories();
    }

    public List<Category> handleViewAllCategories(String sortBy) {
        return categoryService.getAllCategories(sortBy);
    }

    public Category handleGetCategoryById(int id) {
        return categoryService.getCategoryById(id);
    }

    public List<Category> searchCategories(String keyword) {
        return categoryService.searchCategories(keyword);
    }

    public boolean handleCreateCategory(Category category) {
        return categoryService.createCategory(category);
    }

    public boolean handleUpdateCategory(Category category) {
        return categoryService.updateCategory(category);
    }

    public boolean handleArchiveCategory(int id) {
        return categoryService.archiveCategory(id);
    }

    public boolean handleRestoreCategory(int id) {
        return categoryService.restoreCategory(id);
    }

    public boolean handleDeleteCategory(int id) {
        return categoryService.deleteCategory(id);
    }

    public List<Category> handleViewArchivedCategories() {
        return categoryService.getAllArchivedCategories();
    }

    public List<Category> handleViewArchivedCategories(String sortBy) {
        return categoryService.getAllArchivedCategories(sortBy);
    }
}
