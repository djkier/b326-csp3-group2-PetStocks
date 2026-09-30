package com.joysistvi.petstocks.model;

import java.time.LocalDate;

public class Inventory {
    private int id;
    private Product product;
    private int quantity;
    private LocalDate expiration;
    private String batchCode;
    private String remark;

    public Inventory(Product product, int quantity, LocalDate expiration,
                     String batchCode, String remark) {
        this.product = product;
        this.quantity = quantity;
        this.expiration = expiration;
        this.batchCode = batchCode;
        this.remark = remark;
    }

    public Inventory(int id, Product product, int quantity, LocalDate expiration,
                     String batchCode, String remark) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.expiration = expiration;
        this.batchCode = batchCode;
        this.remark = remark;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", expiration=" + expiration +
                ", batchCode='" + batchCode + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
