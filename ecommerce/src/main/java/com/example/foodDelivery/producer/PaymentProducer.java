package com.example.foodDelivery.producer;

import com.example.foodDelivery.event.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentSuccess(OrderEvent order) {
        kafkaTemplate.send("payment-success-topic", order);
    }
}