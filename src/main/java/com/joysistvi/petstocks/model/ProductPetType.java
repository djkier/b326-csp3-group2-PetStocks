package com.joysistvi.petstocks.model;

public class ProductPetType {
    private int id;
    private Product product;
    private PetType petType;

    public ProductPetType(Product product, PetType petType) {
        this.product = product;
        this.petType = petType;
    }

    public ProductPetType(int id, Product product, PetType petType) {
        this.id = id;
        this.product = product;
        this.petType = petType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PetType getPetType() {
        return petType;
    }

    public void setPetType(PetType petType) {
        this.petType = petType;
    }

    @Override
    public String toString() {
        return "ProductPetType{" +
                "id=" + id +
                ", product=" + product +
                ", petType=" + petType +
                '}';
    }
}
