package com.example.foodDelivery.kafka;

import com.example.foodDelivery.event.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public ProductProducer(
            KafkaTemplate<String, OrderEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void releaseStock(OrderEvent order) {

        kafkaTemplate.send(
                "release-stock-topic",
                order
        );
    }
}