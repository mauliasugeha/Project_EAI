package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderCancelledConsumer {

    @KafkaListener(
            topics = "payment-failed-topic",
            groupId = "cancel-group"
    )
    public void cancelOrder(OrderEvent order) {

        System.out.println("=================================");
        System.out.println("❌ ORDER CANCELLED");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Status : " + order.getStatus());
        System.out.println("=================================");
    }
}