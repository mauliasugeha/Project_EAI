package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.producer.PaymentProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;
    private static String lastPaymentStatus = "BELUM ADA";

    public PaymentConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @KafkaListener(topics = "order-topic", groupId = "payment-group")
    public void processPayment(OrderEvent order) {

        System.out.println("💳 PAYMENT SERVICE PROCESSING...");

        order.setStatus("PAID");
        
        paymentProducer.sendPaymentSuccess(order);
        lastPaymentStatus = "PAID - Order " + order.getOrderId();
    }

    public static String getLastPaymentStatus() {
        return lastPaymentStatus;
    }
}