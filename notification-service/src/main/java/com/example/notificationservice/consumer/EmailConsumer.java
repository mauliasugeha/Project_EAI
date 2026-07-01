package com.example.notificationservice.consumer;

import com.example.notificationservice.event.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment-success-topic-v5", groupId = "email-success-group-v5")
    public void receiveSuccessEmail(String message) {
        log.info("📧 EMAIL SERVICE - PAYMENT SUCCESS");

        try {
            OrderEvent order = objectMapper.readValue(message, OrderEvent.class);

            Thread.sleep(1000);

            log.info("===========================================");
            log.info("📧 EMAIL PEMBAYARAN BERHASIL");
            log.info("===========================================");
            log.info("To: {}@example.com", order.getCustomerName().toLowerCase().replaceAll("\\s+",""));
            log.info("Subject: Payment Success - {}", order.getOrderId());
            log.info("");
            log.info("Dear {},", order.getCustomerName());
            log.info("");
            log.info("Pembayaran berhasil diterima.");
            log.info("Order sedang diproses pengiriman.");
            log.info("");
            log.info("Order ID: {}", order.getOrderId());
            log.info("Produk: {}", order.getProductName());
            log.info("===========================================");

        } catch (Exception e) {
            log.error("❌ Gagal parsing JSON sukses: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic-v5", groupId = "email-failed-group-v5")
    public void receiveFailedEmail(String message) {
        log.info("📧 EMAIL SERVICE - PAYMENT FAILED");

        try {
            OrderEvent order = objectMapper.readValue(message, OrderEvent.class);

            Thread.sleep(1000);

            log.info("===========================================");
            log.info("❌ EMAIL PEMBATALAN ORDER");
            log.info("===========================================");
            log.info("To: {}@example.com", order.getCustomerName().toLowerCase().replaceAll("\\s+",""));
            log.info("Subject: Payment Failed - {}", order.getOrderId());
            log.info("");
            log.info("Dear {},", order.getCustomerName());
            log.info("");
            log.info("Maaf, pembayaran order gagal.");
            log.info("Order dibatalkan otomatis.");
            log.info("");
            log.info("Order ID: {}", order.getOrderId());
            log.info("Produk: {}", order.getProductName());
            log.info("Status: CANCELLED");
            log.info("===========================================");

        } catch (Exception e) {
            log.error("❌ Gagal parsing JSON gagal: {}", e.getMessage());
        }
    }
}