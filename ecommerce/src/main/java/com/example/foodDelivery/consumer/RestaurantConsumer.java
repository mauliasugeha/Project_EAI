package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.event.ProductReservedEvent;
import com.example.foodDelivery.kafka.ProductProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RestaurantConsumer {

    private static final Logger log = LoggerFactory.getLogger(RestaurantConsumer.class);

    // Inject ProductProducer untuk mengirim event ke Payment
    private final ProductProducer productProducer;

    // Simulasi database stok
    private static final java.util.Map<String, Integer> menuStock = new java.util.HashMap<>();

    static {
        // Initial stock
        menuStock.put("Nasi Goreng", 50);
        menuStock.put("Mie Ayam", 40);
        menuStock.put("Ayam Geprek", 30);
        menuStock.put("Es Teh", 100);
    }

    // Constructor Injection
    public RestaurantConsumer(ProductProducer productProducer) {
        this.productProducer = productProducer;
    }

    /**
     * 1. SAGA HAPPY PATH: Listener untuk order baru
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

            log.info("✅ Restaurant berhasil memproses order: {}", order.getOrderId());

            // ==========================================
            // SAGA: KIRIM EVENT KE PAYMENT SERVICE
            // ==========================================
            ProductReservedEvent reservedEvent = new ProductReservedEvent(
                    order.getOrderId(),
                    order.getTotalPrice(),
                    order.getCustomerName(),
                    order.getProductName(),
                    order.getQuantity()
            );
            productProducer.sendProductReserved(reservedEvent);
            log.info("⏩ SAGA: Event ProductReserved dikirim ke Kafka untuk diproses Payment");

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
     * 2. SAGA COMPENSATION (ROLLBACK): Kembalikan stok jika payment gagal
     */
    @KafkaListener(topics = "payment-failed-topic", groupId = "restaurant-rollback-group")
    public void rollbackStock(OrderEvent failedOrder) {
        log.warn("===========================================");
        log.warn("🔙 SAGA ROLLBACK: PAYMENT FAILED DETECTED");
        log.warn("Mengembalikan stok untuk Order: {}", failedOrder.getOrderId());

        String product = failedOrder.getProductName();
        int quantityToReturn = failedOrder.getQuantity();

        if (product != null && menuStock.containsKey(product)) {
            int currentStock = menuStock.get(product);
            menuStock.put(product, currentStock + quantityToReturn);

            log.info("🔄 Stok {} dikembalikan sebanyak {}", product, quantityToReturn);
            log.info("📈 Total stok {} kembali normal menjadi: {}", product, menuStock.get(product));
        } else {
            log.error("❌ Gagal rollback: Produk {} tidak dikenali di database", product);
        }
        log.warn("===========================================");
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
            currentStock = 0;
        }

        log.info("Stok saat ini: {} porsi", currentStock);
        log.info("Quantity diminta: {} porsi", requestedQty);

        if (currentStock < requestedQty) {
            throw new IllegalStateException(
                    String.format("Stok %s tidak cukup. Diminta: %d, Tersedia: %d",
                            productName, requestedQty, currentStock)
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

        if (newStock < 10) {
            log.warn("⚠ STOK MENIPIS! {} tersisa {} unit", productName, newStock);
        }
    }

    /**
     * Handler jika stok tidak cukup
     */
    private void handleInsufficientStock(OrderEvent order) {
        log.warn("⚠ Menangani stok tidak cukup untuk order: {}", order.getOrderId());
        log.info("📧 Notifikasi dikirim ke admin untuk restock");
        log.info("📧 Email dikirim ke customer: {}", order.getCustomerName());

        // (Opsional) Jika di masa depan ingin membatalkan order saat stok habis,
        // kamu bisa memanggil productProducer untuk mengirimkan ProductReservationFailedEvent di sini.
    }

    public static java.util.Map<String, Integer> getMenuStock() {
        return new java.util.HashMap<>(menuStock);
    }
}