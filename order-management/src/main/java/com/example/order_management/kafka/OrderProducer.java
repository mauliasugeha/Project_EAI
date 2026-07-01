package com.example.order_management.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(Object order) {
        try {
            String json = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("order-topic-v5", json);
//            log.info("✅ [DEBUG] Berhasil kirim order ke Kafka");
        } catch (Exception e) {
            log.error("❌ Gagal kirim: ", e);
        }
    }
}