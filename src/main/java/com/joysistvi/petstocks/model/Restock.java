package com.joysistvi.petstocks.model;

import java.time.LocalDateTime;

public class Restock {
    private int id;
    private Inventory inventory;
    private Supplier supplier;
    private LocalDateTime datetimeDelivered;
    private int quantityDelivered;
    private int userId;
    private String username;

    public Restock(Inventory inventory, Supplier supplier, LocalDateTime datetimeDelivered,
                   int quantityDelivered, int userId) {
        this.inventory = inventory;
        this.supplier = supplier;
        this.datetimeDelivered = datetimeDelivered;
        this.quantityDelivered = quantityDelivered;
        this.userId = userId;
    }

    public Restock(int id, Inventory inventory, Supplier supplier,
                   LocalDateTime datetimeDelivered, int quantityDelivered,
                   int userId, String username) {
        this.id = id;
        this.inventory = inventory;
        this.supplier = supplier;
        this.datetimeDelivered = datetimeDelivered;
        this.quantityDelivered = quantityDelivered;
        this.userId = userId;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public LocalDateTime getDatetimeDelivered() {
        return datetimeDelivered;
    }

    public void setDatetimeDelivered(LocalDateTime datetimeDelivered) {
        this.datetimeDelivered = datetimeDelivered;
    }

    public int getQuantityDelivered() {
        return quantityDelivered;
    }

    public void setQuantityDelivered(int quantityDelivered) {
        this.quantityDelivered = quantityDelivered;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Restock{" +
                "id=" + id +
                ", inventory=" + inventory +
                ", supplier=" + supplier +
                ", datetimeDelivered=" + datetimeDelivered +
                ", quantityDelivered=" + quantityDelivered +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}
