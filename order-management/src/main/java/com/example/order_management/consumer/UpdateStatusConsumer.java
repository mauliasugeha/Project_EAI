package com.example.order_management.consumer;

import com.example.order_management.dto.OrderEvent;
import com.example.order_management.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateStatusConsumer {
    private static final Logger log = LoggerFactory.getLogger(UpdateStatusConsumer.class);
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UpdateStatusConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-failed-topic-v5", groupId = "order-management-group-v5")
    public void updateOrderStatusToCancelled(String message) {
        try {
//            log.info("📢 [DEBUG] Menerima pesan FAILED sebagai String: {}", message);
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            orderRepository.findByOrderNumber(event.getOrderId()).ifPresentOrElse(order -> {
                order.setStatus("CANCELLED");
                orderRepository.save(order);
                log.warn("📝 [ORDER MANAGEMENT] Status Order {} di-update menjadi CANCELLED", event.getOrderId());
            }, () -> log.error("❌ Order {} tidak ditemukan", event.getOrderId()));

        } catch (Exception e) {
            log.error("❌ Gagal memproses message FAILED: ", e);
        }
    }
}