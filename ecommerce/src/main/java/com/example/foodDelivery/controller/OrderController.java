package com.example.foodDelivery.controller;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.producer.OrderProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {

        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity harus > 0");
        }

        if (request.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Price harus > 0");
        }

        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                request.getCustomerName(),
                request.getRestaurantName(),
                request.getProductName(),
                request.getQuantity(),
                request.getTotalPrice(),
                LocalDateTime.now()
        );

        orderProducer.sendOrder(event);

        return ResponseEntity.ok("Order berhasil dibuat dengan ID: " + event.getOrderId());
    }

    // DTO request tetap sama
    public static class OrderRequest {
        private String customerName;
        private String restaurantName;
        private String productName;
        private int quantity;
        private BigDecimal totalPrice;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }
}