package com.example.foodDelivery.controller;
import com.example.foodDelivery.model.Order;
import com.example.foodDelivery.producer.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;
@RestController
@RequestMapping("/api")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderProducer orderProducer;
    // Constructor Injection
    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }
    // Endpoint untuk membuat order
    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request)
    {
        // Validasi input
        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity harus > 0");
        }
        if (request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Price harus > 0");
        }
        // Buat order
        Order order = new Order(
                UUID.randomUUID().toString(),
                request.getCustomerName(),
                request.getRestaurantName(),
                request.getProductName(),
                request.getQuantity(),
                request.getPrice()
        );
        // Kirim ke RabbitMQ
        orderProducer.sendOrder(order);
        return ResponseEntity.ok("Order berhasil dibuat dengan ID: " + order.getOrderId());
    }


//    @PostMapping("/order")
//    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
//        log.info("Menerima request order dari customer: {}", request.getCustomerName());
//
//        // Buat objek Order
//        Order order = new Order(
//                UUID.randomUUID().toString(), // Generate ID unik
//                request.getCustomerName(),
//                request.getProductName(),
//                request.getQuantity(),
//                request.getPrice()
//        );
//
//        // Kirim ke RabbitMQ
//
//        orderProducer.sendOrder(order);
//
//        log.info("Order {} berhasil dibuat dan dikirim ke queue", order.getOrderId());
//
//        return ResponseEntity.ok("Order berhasil dibuat dengan ID: " + order.getOrderId());
//    }
    // Inner class untuk request body
    public static class OrderRequest {
        private String customerName;
        private String restaurantName;
        private String productName;
        private int quantity;
        private BigDecimal price;
        // Getter dan Setter
        public String getCustomerName() {
            return customerName;
        }
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getRestaurantName(){
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName){
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
    }
}