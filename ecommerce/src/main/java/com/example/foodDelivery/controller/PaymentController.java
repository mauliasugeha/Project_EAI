package com.example.foodDelivery.controller;

import com.example.foodDelivery.consumer.PaymentConsumer;
import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.producer.OrderProducer;
import com.example.foodDelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProducer orderProducer;

    @PostMapping("/pay/{orderId}")
    public String payOrder(@PathVariable String orderId) {

        OrderEvent order = orderService.getOrder(orderId);

        if (order == null) {
            return "Order tidak ditemukan";
        }

        if ("PAID".equals(order.getStatus())) {
            return "Order sudah dibayar";
        }

        order.setStatus("PAID");

        System.out.println("=================================");
        System.out.println("💳 PEMBAYARAN BERHASIL");
        System.out.println("Order ID: " + orderId);
        System.out.println("=================================");

        orderProducer.sendOrder(order);

        return "Pembayaran berhasil untuk order: " + orderId;
    }

    @GetMapping("/status")
    public String getPaymentStatus() {
        return PaymentConsumer.getLastPaymentStatus();
    }
}