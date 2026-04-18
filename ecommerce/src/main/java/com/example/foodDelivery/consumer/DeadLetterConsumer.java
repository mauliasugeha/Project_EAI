package com.example.foodDelivery.consumer;

import com.example.foodDelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
@Service
public class DeadLetterConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);
    @RabbitListener(queues = "${rabbitmq.dlq}")
    public void handleDeadLetter(
            Order order,
            @Header(name = "x-death", required = false) List<Map<String, Object>> death) {
        log.error("❌ Pesan masuk Dead Letter Queue");
        log.error("Order ID: {}", order != null ? order.getOrderId() : "unknown");
        if (death != null && !death.isEmpty()) {
            log.error("Informasi x-death: {}", death);
        }
        // Simpan ke database untuk investigasi
        saveFailedOrder(order);
        // Kirim alert ke admin
        sendAlertToAdmin(order);
    }

    private void saveFailedOrder(Order order) {
        log.info("Menyimpan order {} ke database untuk investigasi",
                order != null ? order.getOrderId() : "unknown");
    }
    private void sendAlertToAdmin(Order order) {
        log.warn("📧 Alert dikirim ke admin");
    }
}
