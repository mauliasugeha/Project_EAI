package com.example.restaurantservice.consumer;

import com.example.restaurantservice.event.OrderEvent;
import com.example.restaurantservice.event.ProductReservedEvent;
import com.example.restaurantservice.producer.ProductProducer;
import com.example.restaurantservice.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RestaurantConsumer {
    private static final Logger log = LoggerFactory.getLogger(RestaurantConsumer.class);

    private final ProductProducer productProducer;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestaurantConsumer(ProductProducer productProducer, ProductRepository productRepository) {
        this.productProducer = productProducer;
        this.productRepository = productRepository;
        log.info("🚀 RestaurantConsumer siap mendengarkan topik!");
    }

    @KafkaListener(topics = "order-topic-v5", groupId = "restaurant-123")
    public void receiveOrder(String message) {
        try {
            Map<String, Object> orderData = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});

            String orderId = String.valueOf(orderData.get("orderId"));
            String customerName = String.valueOf(orderData.get("customerName"));
            String productName = String.valueOf(orderData.get("productName"));
            int quantity = orderData.get("quantity") != null ? ((Number) orderData.get("quantity")).intValue() : 0;
            java.math.BigDecimal totalPrice = orderData.get("totalPrice") != null ?
                    java.math.BigDecimal.valueOf(((Number) orderData.get("totalPrice")).doubleValue()) :
                    java.math.BigDecimal.ZERO;

            log.info("=========================================");
            log.info("🔍 [INVENTORY] Memvalidasi ketersediaan stok untuk produk: {}", productName);

            productRepository.findByName(productName).ifPresentOrElse(product -> {
                if (product.getStock() >= quantity) {
                    log.info("✅ [INVENTORY] Stok tersedia. Menunggu pembayaran...");
                    ProductReservedEvent reservedEvent = new ProductReservedEvent(
                            orderId, totalPrice, customerName, productName, quantity
                    );
                    productProducer.sendProductReserved(reservedEvent);
                } else {
                    log.warn("❌ [INVENTORY] Stok HABIS! Diminta: {}, Sisa: {}", quantity, product.getStock());

                    OrderEvent failedEvent = new OrderEvent();
                    failedEvent.setOrderId(orderId);
                    failedEvent.setCustomerName(customerName);
                    failedEvent.setProductName(productName);
                    failedEvent.setQuantity(quantity);
                    failedEvent.setTotalPrice(totalPrice);
                    failedEvent.setStatus("FAILED");

                    productProducer.sendPaymentFailed(failedEvent);
                }
            }, () -> {
                log.error("❌ [INVENTORY] Produk '{}' tidak terdaftar di restoran.", productName);

                OrderEvent failedEvent = new OrderEvent();
                failedEvent.setOrderId(orderId);
                failedEvent.setCustomerName(customerName);
                failedEvent.setProductName(productName);
                failedEvent.setQuantity(quantity);
                failedEvent.setStatus("FAILED");
                productProducer.sendPaymentFailed(failedEvent);
            });

        } catch (Exception e) {
            log.error("❌ Gagal memproses pesan order di RestaurantConsumer: ", e);
        }
    }

    @KafkaListener(topics = "payment-success-topic-v5", groupId = "restaurant-cut-stock-group-v5")
    public void cutStockOnPaymentSuccess(String message) {
        try {
            OrderEvent successfulOrder = objectMapper.readValue(message, OrderEvent.class);
            log.info("💳 [INVENTORY] Mendapat notifikasi PEMBAYARAN SUKSES untuk Order: {}", successfulOrder.getOrderId());

            productRepository.findByName(successfulOrder.getProductName()).ifPresent(product -> {
                int newStock = product.getStock() - successfulOrder.getQuantity();
                product.setStock(newStock);
                productRepository.save(product);
                log.info("✂️ [INVENTORY] Stok '{}' dipotong. Sisa: {}", successfulOrder.getProductName(), newStock);
            });
        } catch (Exception e) {
            log.error("Gagal parsing JSON: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic-v5", groupId = "restaurant-rollback-group-v5")
    public void handleFailedPaymentLog(String message) {
        try {
            OrderEvent failedOrder = objectMapper.readValue(message, OrderEvent.class);
            log.warn("⚠️ [RESTAURANT] Pembayaran order {} GAGAL.", failedOrder.getOrderId());
        } catch (Exception e) {
            log.error("❌ Gagal parsing event: {}", e.getMessage());
        }
    }
}