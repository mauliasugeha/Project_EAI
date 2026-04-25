package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "payment-group")
    public void receiveOrder(OrderEvent order) {

        log.info("===========================================");
        log.info("Order ID: {}", order.getOrderId());
        log.info("Customer: {}", order.getCustomerName());
        log.info("Produk: {}", order.getProductName());
        log.info("Quantity: {}", order.getQuantity());
        log.info("Price: Rp {}", order.getTotalPrice());
        log.info("Total: Rp {}", order.getTotalPrice()
                .multiply(new BigDecimal(order.getQuantity())));
        log.info("Status: {}", order.getStatus());
        log.info("===========================================");

        try {
            validateOrder(order);
            processPayment(order);

            log.info("✅ Pembayaran berhasil untuk order: {}",
                    order.getOrderId());

        } catch (Exception e) {
            log.error("❌ Error processing payment", e);
        }
    }

    private void validateOrder(OrderEvent order) {

        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            throw new IllegalArgumentException("Order ID kosong");
        }

        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            throw new IllegalArgumentException("Customer kosong");
        }

        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity invalid");
        }

        if (order.getTotalPrice() == null ||
                order.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price invalid");
        }

        log.info("✅ Validasi sukses");
    }

    private void processPayment(OrderEvent order) {

        log.info("Memproses pembayaran...");

        BigDecimal total = order.getTotalPrice()
                .multiply(new BigDecimal(order.getQuantity()));

        log.info("Total transaksi: Rp {}", total);

        log.info("✅ Payment selesai");
    }
}