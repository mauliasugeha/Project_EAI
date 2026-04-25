package com.example.foodDelivery.kafka;

import org.jspecify.annotations.Nullable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.foodDelivery.event.OrderEvent;

@Service
public class ProductProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public ProductProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(@Nullable OrderEvent message) {
        kafkaTemplate.send("product-topic", message);
    }
}