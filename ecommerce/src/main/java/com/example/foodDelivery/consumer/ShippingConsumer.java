package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingConsumer {

    private static final Map<String, String> shippingStatus =
            new HashMap<>();

    @KafkaListener(
            topics = "payment-success-topic",
            groupId = "shipping-group"
    )
    public void processShipping(OrderEvent order) {

        if ("CANCELLED".equals(order.getStatus())) {

            shippingStatus.put(
                    order.getOrderId(),
                    "SHIPPING DIBATALKAN"
            );

            System.out.println("===================================");
            System.out.println("🚫 SHIPPING DIBATALKAN");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("===================================");

            return;
        }

        order.setStatus("SHIPPED");

        shippingStatus.put(
                order.getOrderId(),
                "SHIPPED"
        );

        System.out.println("===================================");
        System.out.println("🚚 SHIPPING SERVICE");
        System.out.println("Order dikirim: " + order.getOrderId());
        System.out.println("===================================");

        BigDecimal total = order.getTotalPrice()
                .multiply(new BigDecimal(order.getQuantity()));

        System.out.println("=========== NOTA DIGITAL ==========");
        System.out.println("Customer : " + order.getCustomerName());
        System.out.println("Produk   : " + order.getProductName());
        System.out.println("Qty      : " + order.getQuantity());
        System.out.println("Total    : Rp " + total);
        System.out.println("Status   : " + order.getStatus());
        System.out.println("===================================");
    }

    public static String getShippingStatus(String orderId) {
        return shippingStatus.getOrDefault(
                orderId,
                "ORDER FAILED / SHIPPING DIBATALKAN"
        );
    }
}