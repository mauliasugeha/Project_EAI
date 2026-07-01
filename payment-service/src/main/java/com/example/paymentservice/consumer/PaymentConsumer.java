package com.example.paymentservice.consumer;

import com.example.paymentservice.event.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    private static String lastPaymentStatus = "BELUM ADA";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment-success-topic-v5", groupId = "payment-log-group-v5")
    public void consumeSuccess(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            String orderId = event.getOrderId();
            lastPaymentStatus = "PAID - Order " + orderId + " berhasil dibayar.";

            log.info("=========================================");
            log.info("💳 [PAYMENT] Memproses pembayaran untuk Order: {}", orderId);
            log.info("✅ [PAYMENT] Saldo mencukupi. Pembayaran BERHASIL!");
            log.info("=========================================");
        } catch (Exception e) {
            log.error("❌ Gagal memproses pesan sukses: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic-v5", groupId = "payment-log-group-v5")
    public void consumeFailed(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            String orderId = event.getOrderId();
            lastPaymentStatus = "FAILED - Order " + orderId + " dibatalkan (Kompensasi Saga).";

            log.warn("❌ [PAYMENT] Pembayaran gagal atau dibatalkan untuk Order: {}", orderId);
        } catch (Exception e) {
            log.error("❌ Gagal memproses pesan gagal: {}", e.getMessage());
        }
    }

    public static String getLastPaymentStatus() {
        return lastPaymentStatus;
    }
}