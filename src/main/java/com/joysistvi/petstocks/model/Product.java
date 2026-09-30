package com.joysistvi.petstocks.model;

public class Product {
    private int id;
    private String name;
    private String brand;
    private String description;
    private boolean isArchived;
    private Category category;

    public Product(String name, String brand, String description, Category category) {
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.category = category;
    }

    public Product(int id, String name, String brand, String description, Category category) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.category = category;
    }

    public Product(int id, String name, String brand, String description, boolean isArchived,
                   Category category) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.isArchived = isArchived;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", description='" + description + '\'' +
                ", isArchived=" + isArchived +
                ", category=" + category +
                '}';
    }
}
