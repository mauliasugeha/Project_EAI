package com.example.paymentservice.controller;

import com.example.paymentservice.consumer.PaymentConsumer;
import com.example.paymentservice.event.OrderEvent;
import com.example.paymentservice.producer.PaymentProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentProducer paymentProducer;

    @PostMapping("/pay/{orderId}")
    public String payOrder(@PathVariable String orderId, @RequestBody OrderEvent orderRequest) {

        orderRequest.setOrderId(orderId);

        if ("FAILED".equalsIgnoreCase(orderRequest.getCustomerName())) {
            orderRequest.setStatus("CANCELLED");
            paymentProducer.sendPaymentFailed(orderRequest);

            return "Pembayaran Gagal! Event CANCELLED dikirim ke Kafka. Saga Kompensasi dimulai.";
        }

        orderRequest.setStatus("PAID");
        paymentProducer.sendPaymentSuccess(orderRequest);

        return "Pembayaran Sukses untuk Order: " + orderId;
    }

    @GetMapping("/status")
    public String getPaymentStatus() {
        return PaymentConsumer.getLastPaymentStatus();
    }
}