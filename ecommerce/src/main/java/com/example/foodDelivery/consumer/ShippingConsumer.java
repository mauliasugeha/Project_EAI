package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ShippingConsumer {
    private static String lastShippingStatus = "BELUM ADA";

    @KafkaListener(
            topics = "payment-success-topic",
            groupId = "shipping-group"
    )
    public void processShipping(OrderEvent order) {

        order.setStatus("SHIPPED");
        lastShippingStatus = "SHIPPED - Order " + order.getOrderId();

        System.out.println("===================================");
        System.out.println("🚚 SHIPPING SERVICE");
        System.out.println("Order dikirim: " + order.getOrderId());
        System.out.println("===================================");

        java.math.BigDecimal total = order.getTotalPrice()
                .multiply(new java.math.BigDecimal(order.getQuantity()));

        System.out.println("=========== NOTA DIGITAL ==========");
        System.out.println("Customer : " + order.getCustomerName());
        System.out.println("Produk   : " + order.getProductName());
        System.out.println("Qty      : " + order.getQuantity());
        System.out.println("Total    : Rp " + total);
        System.out.println("Status   : " + order.getStatus());
        System.out.println("===================================");
    }

    public static String getLastShippingStatus() {
        return lastShippingStatus;
    }
}