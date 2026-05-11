package com.example.foodDelivery.producer;

import com.example.foodDelivery.event.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(
            KafkaTemplate<String, OrderEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(OrderEvent order) {

        kafkaTemplate.send(
                "order-topic",
                order
        );
    }
}