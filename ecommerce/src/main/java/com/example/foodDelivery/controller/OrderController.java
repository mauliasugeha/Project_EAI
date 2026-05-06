package com.example.foodDelivery.controller;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.producer.OrderProducer;
import com.example.foodDelivery.model.Order;
import com.example.foodDelivery.repository.OrderRepository;
import com.example.foodDelivery.service.OrderService;

import com.example.foodDelivery.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    public OrderController(OrderProducer orderProducer, OrderRepository orderRepository, OrderService orderService) {
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {

        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity harus > 0");
        }

        if (request.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Price harus > 0");
        }

        // 1. simpan ke DB
        Order order = new Order(
                UUID.randomUUID().toString(),
                request.getCustomerName(),
                request.getRestaurantName(),
                request.getProductName(),
                request.getQuantity(),
                request.getTotalPrice()
        );

        orderRepository.save(order);

        // 2. kirim ke Kafka (INI YANG KAMU HAPUS TADI)
        OrderEvent event = new OrderEvent(
                order.getOrderId(),
                order.getCustomerName(),
                order.getRestaurantName(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                LocalDateTime.now()
        );
        event.setStatus("CREATED");

        orderService.createOrder(event);

//        orderProducer.sendOrder(event);

        return ResponseEntity.ok("Order disimpan & event dikirim ke Kafka");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // DTO request
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