package com.example.order_management.consumer;

import com.example.order_management.entity.Order;
import com.example.order_management.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ShippingConsumer {

    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(
            topics = "shipping-success-topic-v5",
            groupId = "order-management-group-v5",
            properties = {
                    "value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"
            }
    )
    public void processShippingUpdate(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);

        if (order != null) {
            order.setStatus("SHIPPED");
            orderRepository.save(order);

            System.out.println("===================================");
            System.out.println("STATUS PESANAN");
            System.out.println("Order ID : " + orderNumber);
            System.out.println("Pesan    : Status pesanan telah diperbarui menjadi SHIPPED");
            System.out.println("===================================");
        } else {
            System.out.println("Error: Order ID " + orderNumber + " tidak ditemukan di database.");
        }
    }

    public String getShippingStatus(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);

        if (order != null) {
            return order.getStatus();
        } else {
            return "ORDER TIDAK DITEMUKAN";
        }
    }
}