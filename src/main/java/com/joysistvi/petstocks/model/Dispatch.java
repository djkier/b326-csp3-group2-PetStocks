package com.joysistvi.petstocks.model;

import java.time.LocalDateTime;

public class Dispatch {
    private int id;
    private Inventory inventory;
    private LocalDateTime datetimeDispatched;
    private int quantityDispatched;
    private int userId;
    private String username;

    public Dispatch(Inventory inventory, LocalDateTime datetimeDispatched,
                    int quantityDispatched, int userId) {
        this.inventory = inventory;
        this.datetimeDispatched = datetimeDispatched;
        this.quantityDispatched = quantityDispatched;
        this.userId = userId;
    }

    public Dispatch(int id, Inventory inventory, LocalDateTime datetimeDispatched,
                    int quantityDispatched, int userId, String username) {
        this.id = id;
        this.inventory = inventory;
        this.datetimeDispatched = datetimeDispatched;
        this.quantityDispatched = quantityDispatched;
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

    public LocalDateTime getDatetimeDispatched() {
        return datetimeDispatched;
    }

    public void setDatetimeDispatched(LocalDateTime datetimeDispatched) {
        this.datetimeDispatched = datetimeDispatched;
    }

    public int getQuantityDispatched() {
        return quantityDispatched;
    }

    public void setQuantityDispatched(int quantityDispatched) {
        this.quantityDispatched = quantityDispatched;
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
        return "Dispatch{" +
                "id=" + id +
                ", inventory=" + inventory +
                ", datetimeDispatched=" + datetimeDispatched +
                ", quantityDispatched=" + quantityDispatched +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}
