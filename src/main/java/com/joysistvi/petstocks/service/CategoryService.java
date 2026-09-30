package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    List<Category> getAllCategories(String sortBy);
    Category getCategoryById(int id);
    List<Category> searchCategories(String keyword);
    boolean createCategory(Category category);
    boolean updateCategory(Category category);
    boolean archiveCategory(int id);
    boolean restoreCategory(int id);
    boolean deleteCategory(int id);
    List<Category> getAllArchivedCategories();
    List<Category> getAllArchivedCategories(String sortBy);
}
