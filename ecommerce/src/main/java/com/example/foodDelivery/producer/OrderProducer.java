package com.example.foodDelivery.producer;

import com.example.foodDelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange}")
    private String orderExchange;
    @Value("${rabbitmq.routingkey}")
    private String orderRoutingKey;
    // Constructor Injection
    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    /**
     * Mengirim order ke RabbitMQ
     * @param order Objek order yang akan dikirim
     */
//    public void sendOrder(Order order) {
//        try {
//            log.info("===========================================");
//            log.info("Mengirim order ke RabbitMQ");
//            log.info("Order ID: {}", order.getOrderId());
//            log.info("Customer: {}", order.getCustomerName());
//            log.info("Produk: {} x {}", order.getProductName(), order.getQuantity());
//            log.info("Total: Rp {}", order.getPrice().multiply(
//                    new java.math.BigDecimal(order.getQuantity())));
//            log.info("===========================================");
//            // Buat correlation data untuk tracking
//            CorrelationData correlationData = new CorrelationData(order.getOrderId());
//            // Kirim pesan ke Fanout Exchange
//            // Routing key diisi string kosong "" — Fanout Exchange mengabaikan routing key
//            rabbitTemplate.convertAndSend(
//                    orderExchange,
//                    "",
//                    order,
//                    correlationData
//            );
//            log.info("✅ Order {} berhasil dikirim ke exchange {}",
//                    order.getOrderId(), orderExchange);
//        } catch (AmqpException e) {
//            log.error("❌ Gagal mengirim order {}: {}", order.getOrderId(), e.getMessage());
//            log.error("Stack trace:", e);
//            throw e;
//        } catch (Exception e) {
//            log.error("❌ Error tidak diketahui saat mengirim order: {}", e.getMessage());
//            log.error("Stack trace:", e);
//            throw new RuntimeException("Gagal mengirim order", e);
//        }
//    }
    /**
     * Mengirim pesan teks sederhana (untuk testing)
     * @param message Pesan teks
     */
    public void sendSimpleMessage(String message) {
        log.info("Mengirim pesan sederhana: {}", message);
        try {
            rabbitTemplate.convertAndSend(orderExchange, "", message);
            log.info("Pesan sederhana berhasil dikirim");
        } catch (Exception e) {
            log.error("Gagal mengirim pesan: {}", e.getMessage());
            throw e;
        }
    }

    public void sendOrder(Order order) {
        // Retry mechanism (3 kali percobaan)
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                rabbitTemplate.convertAndSend(orderExchange, orderRoutingKey, order);
                log.info("Order berhasil dikirim");
                return; // Keluar dari loop jika sukses

            } catch (AmqpException e) {
                retryCount++;
                log.warn("Gagal mengirim (percobaan {}/{}): {}",
                        retryCount, maxRetries, e.getMessage());

                if (retryCount == maxRetries) {
                    log.error("Gagal mengirim order setelah {} percobaan", maxRetries);
                    // Simpan ke database untuk diproses nanti
                    saveOrderForLater(order);
                    throw e;
                }

                // Tunggu 1 detik sebelum retry
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void saveOrderForLater(Order order) {
        log.info("Menyimpan order {} untuk diproses nanti", order.getOrderId
                ());
        // Implementasi: simpan ke database dengan status PENDING
    }
}