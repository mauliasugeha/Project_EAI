package com.example.foodDelivery.consumer;

import com.example.foodDelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class DeliveryConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeliveryConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.delivery}")
    public void receiveOrder(Order order) {
        log.info("🚚 Delivery menerima order: {}", order.getOrderId());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("✅ Order sedang dikirim ke customer");
    }
}