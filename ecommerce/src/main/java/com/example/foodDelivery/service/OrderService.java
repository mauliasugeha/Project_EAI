package com.example.foodDelivery.service;

import com.example.foodDelivery.event.OrderEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    public static Map<String, OrderEvent> orderStore = new HashMap<>();

    public OrderEvent createOrder(OrderEvent order) {

        String id = UUID.randomUUID().toString();
        order.setOrderId(id);
        order.setStatus("CREATED");

        orderStore.put(id, order);

        System.out.println("=================================");
        System.out.println("🧾 ORDER DIBUAT");
        System.out.println("ID: " + id);
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Status: MENUNGGU PEMBAYARAN");
        System.out.println("=================================");

        return order;
    }

    public OrderEvent getOrder(String id) {
        return orderStore.get(id);
    }
}