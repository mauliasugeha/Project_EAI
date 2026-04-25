package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RestaurantConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(RestaurantConsumer.class);

    // Simulasi database stok
    // Dalam aplikasi nyata, ini dari database
    private static final java.util.Map<String, Integer> menuStock =
            new java.util.HashMap<>();

    static {
        // Initial stock
        menuStock.put("Nasi Goreng", 50);
        menuStock.put("Mie Ayam", 40);
        menuStock.put("Ayam Geprek", 30);
        menuStock.put("Es Teh", 100);
    }

    /**
     * Listener untuk inventory queue
     */
    @KafkaListener(topics = "order-topic", groupId = "restaurant-group")
    public void receiveOrder(OrderEvent order) {
        log.info("===========================================");
        log.info("Order ID: {}", order.getOrderId());
        log.info("Produk: {}", order.getProductName());
        log.info("Quantity: {}", order.getQuantity());
        log.info("===========================================");

        try {
            // Cek stok
            checkStock(order);

            // Update stok
            updateStock(order);

            log.info("✅ Restaurant berhasil memproses order: {}",
                    order.getOrderId());

        } catch (IllegalStateException e) {
            // Stok tidak cukup
            log.error("❌ Stok tidak cukup: {}", e.getMessage());
            handleInsufficientStock(order);

        } catch (Exception e) {
            log.error("❌ Error saat update inventory: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Cek ketersediaan stok
     */
    private void checkStock(OrderEvent order) {
        String productName = order.getProductName();
        int requestedQty = order.getQuantity();
        Integer currentStock = menuStock.get(productName);

        if (currentStock == null) {
            log.warn("Produk {} tidak ditemukan dalam inventory", productName);
            // Anggap stok 0
            currentStock = 0;
        }

        log.info("Stok saat ini: {} unit", currentStock);
        log.info("Quantity diminta: {} unit", requestedQty);
        log.info("DEBUG productName: [{}]", menuStock);

        if (currentStock < requestedQty) {
            throw new IllegalStateException(
                    String.format(
                            "Stok %s tidak cukup. Diminta: %d, Tersedia: %d",
                            productName, requestedQty, currentStock
                    )
            );
        }

        log.info("✅ Stok mencukupi");
    }

    /**
     * Update stok setelah order
     */
    private void updateStock(OrderEvent order) {
        String productName = order.getProductName();
        int quantity = order.getQuantity();
        Integer currentStock = menuStock.getOrDefault(productName, 0);
        int newStock = currentStock - quantity;

        menuStock.put(productName, newStock);

        log.info("📉 Stok diupdate:");
        log.info(" Produk: {}", productName);
        log.info(" Sebelum: {}", currentStock);
        log.info(" Setelah: {}", newStock);
        log.info(" Berkurang: {}", quantity);

        // Warning jika stok menipis
        if (newStock < 10) {
            log.warn("⚠ STOK MENIPIS! {} tersisa {} unit",
                    productName, newStock);
        }
    }

    /**
     * Handler jika stok tidak cukup
     */
    private void handleInsufficientStock(OrderEvent order) {
        log.warn("⚠ Menangani stok tidak cukup untuk order: {}",
                order.getOrderId());

        // Dalam aplikasi nyata:
        // 1. Kirim notifikasi ke admin
        // 2. Update order status menjadi OUT_OF_STOCK
        // 3. Kirim email ke customer

        log.info("📧 Notifikasi dikirim ke admin untuk restock");
        log.info("📧 Email dikirim ke customer: {}",
                order.getCustomerName());
    }

    /**
     * Method untuk melihat stok (untuk testing)
     */
    public static java.util.Map<String, Integer> getMenuStock() {
        return new java.util.HashMap<>(menuStock);
    }
}