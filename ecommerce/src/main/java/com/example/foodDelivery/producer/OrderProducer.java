package com.example.foodDelivery.producer;

import com.example.foodDelivery.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(OrderEvent order) {
//        log.info("📤 Sending order to Kafka: {}", event.getOrderId());

        kafkaTemplate.send("order-topic", order);

//        log.info("✅ Order sent to Kafka topic");
    }
}