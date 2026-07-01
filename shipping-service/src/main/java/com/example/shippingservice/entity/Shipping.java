package com.example.shippingservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipping")
public class Shipping {

    @Id
    private String orderId; // Menggunakan ID yang sama dengan pesanan (Primary Key)

    private String status;
    private String customerName; // Opsional: untuk keperluan nota
    private String productName;  // Opsional: untuk keperluan nota

    // Constructor Kosong (Wajib untuk JPA)
    public Shipping() {}

    // Constructor dengan Parameter
    public Shipping(String orderId, String status, String customerName, String productName) {
        this.orderId = orderId;
        this.status = status;
        this.customerName = customerName;
        this.productName = productName;
    }

    // Getter dan Setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}