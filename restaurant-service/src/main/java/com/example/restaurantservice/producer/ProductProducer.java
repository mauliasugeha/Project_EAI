package com.example.restaurantservice.producer;

import com.example.restaurantservice.event.OrderEvent;
import com.example.restaurantservice.event.ProductReservedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductProducer {
    private static final Logger log = LoggerFactory.getLogger(ProductProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentFailed(OrderEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-failed-topic-v5", json);
//            log.info("📢 [PRODUCER] Mengirim event Payment Failed ke topik v5");
        } catch (Exception e) {
            log.error("Gagal kirim: ", e);
        }
    }

    public void sendProductReserved(ProductReservedEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("product-reserved-topic-v5", jsonMessage);
//            log.info("✅ [PRODUCER] Mengirim event Product Reserved ke topik v5");
        } catch (Exception e) {
            log.error("Gagal convert ke JSON: {}", e.getMessage());
        }
    }
}