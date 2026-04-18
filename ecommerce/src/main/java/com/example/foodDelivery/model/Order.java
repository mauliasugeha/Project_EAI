package com.example.foodDelivery.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String customerName;
    private String restaurantName;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime orderDate;

    // Constructor (tanpa parameter)
    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    // Constructor (dengan parameter)
    public Order(
            String orderId,
            String customerName,
            String restaurantName,
            String productName,
            int quantity,
            BigDecimal price
    ) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.restaurantName = restaurantName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = LocalDateTime.now();
    }

    // Getter dan Setter
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    // Method untuk menampilkan informasi order
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderDate=" + orderDate +
                '}';
    }
}