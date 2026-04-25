package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class DeliveryConsumer {

    @KafkaListener(topics = "order-topic", groupId = "delivery-group")
    public void receiveOrder(OrderEvent order) {

        System.out.println("🚚 DELIVERY menerima order: " + order.getOrderId());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Order sedang dikirim ke customer");
    }
}