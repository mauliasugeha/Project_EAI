package com.example.foodDelivery.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.foodDelivery.event.OrderEvent;

@Service
public class OrderConsumer {

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(OrderEvent event) {

        System.out.println("📩 ORDER MASUK:");
        System.out.println("ID: " + event.getOrderId());
        System.out.println("Customer: " + event.getCustomerName());
        System.out.println("Restaurant: " + event.getRestaurantName());
    }
}