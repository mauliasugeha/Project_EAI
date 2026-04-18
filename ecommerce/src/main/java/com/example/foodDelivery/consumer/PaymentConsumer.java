package com.example.foodDelivery.consumer;

import com.example.foodDelivery.model.Order;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentConsumer.class);

    /**
     * Listener untuk payment queue dengan MANUAL ACK
     */
    @RabbitListener(
            queues = "${rabbitmq.queue.payment}",
            containerFactory = "retryContainerFactory"
    )
    public void receiveOrder(Order order,
                             Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        log.info("===========================================");
        log.info("Order ID: {}", order.getOrderId());
        log.info("Customer: {}", order.getCustomerName());
        log.info("Restaurant: {}", order.getRestaurantName());
        log.info("Produk: {}", order.getProductName());
        log.info("Quantity: {}", order.getQuantity());
        log.info("Price: Rp {}", order.getPrice());
        log.info("Total: Rp {}", order.getPrice().multiply(
                new BigDecimal(order.getQuantity())));
        log.info("Order Date: {}", order.getOrderDate());
        log.info("===========================================");

        try {
            // 1. Validasi order
            validateOrder(order);

            // 2. Proses pembayaran
            processPayment(order);

            log.info("✅ Pembayaran berhasil untuk order: {}",
                    order.getOrderId());

            // ✅ ACK (hapus dari queue)
            channel.basicAck(deliveryTag, false);
            log.info("✅ Message acknowledged");

        } catch (IllegalArgumentException e) {
            // ❌ Validasi gagal → langsung ke DLQ
            log.error("❌ Validasi gagal: {}", e.getMessage());

            try {
                channel.basicNack(deliveryTag, false, false);
                log.warn("⚠ Message dikirim ke DLQ");
            } catch (Exception ex) {
                log.error("Gagal mengirim ke DLQ", ex);
            }

        } catch (Exception e) {
            // ❌ Error lain → retry
            log.error("❌ Error saat proses payment: {}", e.getMessage());
            log.error("Stack trace:", e);

            try {
                channel.basicNack(deliveryTag, false, true);
                log.warn("🔁 Message akan di-retry");
            } catch (Exception ex) {
                log.error("Gagal retry message", ex);
            }
        }
    }

    /**
     * Validasi order sebelum diproses
     */
    private void validateOrder(Order order) {
        log.info("Memvalidasi order...");

        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }

        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            throw new IllegalArgumentException("Customer name tidak boleh kosong");
        }

        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih dari 0");
        }

        if (order.getPrice() == null ||
                order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price harus lebih dari 0");
        }

        log.info("✅ Validasi berhasil");
    }

    /**
     * Simulasi proses pembayaran
     */
    private void processPayment(Order order) {
        log.info("Memproses pembayaran...");

        // Step 1: Verifikasi data customer
        log.info("Step 1: Verifikasi data customer");
        simulateDelay(1000, "Verifikasi customer");

        // Step 2: Cek payment method
        log.info("Step 2: Cek payment method");
        simulateDelay(500, "Cek payment method");

        // Step 3: Proses transaksi
        log.info("Step 3: Proses transaksi");
        BigDecimal total = order.getPrice().multiply(
                new BigDecimal(order.getQuantity()));
        log.info("Total transaksi: Rp {}", total);
        simulateDelay(1000, "Proses transaksi");

        // Step 4: Generate receipt
        log.info("Step 4: Generate receipt");
        String receiptNumber =
                "PAY-" + order.getOrderId().substring(0, 8).toUpperCase();
        log.info("Receipt Number: {}", receiptNumber);
        simulateDelay(500, "Generate receipt");

        log.info("✅ Pembayaran selesai - Receipt: {}", receiptNumber);
    }

    /**
     * Simulasi delay
     */
    private void simulateDelay(long millis, String processName) {
        try {
            log.info("⏳ {} ({} ms)...", processName, millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Process interrupted: {}", processName);
        }
    }
}