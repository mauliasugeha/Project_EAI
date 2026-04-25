package com.example.foodDelivery.consumer;

import com.example.foodDelivery.event.OrderEvent;
import com.example.foodDelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class EmailConsumer {
    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    @KafkaListener(topics = "order-topic", groupId = "email-group")
    public void receiveOrder(OrderEvent order) {
        log.info("📧 EMAIL SERVICE - Menerima Order: {}", order.getOrderId());

        try {
            sendOrderConfirmationEmail(order);
            log.info("✅ Email konfirmasi terkirim ke {}", order.getCustomerName());

        } catch (Exception e) {
            log.error("❌ Gagal kirim email: {}", e.getMessage());
            // Email failure tidak critical, log saja
        }
    }

    private void sendOrderConfirmationEmail(OrderEvent order) {
        log.info("Mengirim email ke customer...");

        // Simulasi delay kirim email
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("===========================================");
        log.info("📧 EMAIL CONTENT");
        log.info("===========================================");
        log.info("To: customer@example.com");
        log.info("Subject: Order Confirmation - {}", order.getOrderId());
        log.info("");
        log.info("Dear {},", order.getCustomerName());
        log.info("");
        log.info("Thank you for your order!");
        log.info("");
        log.info("Order Details:");
        log.info(" Restaurant Name: {}", order.getRestaurantName());
        log.info(" Product: {}", order.getProductName());
        log.info(" Quantity: {}", order.getQuantity());
        log.info(" Price: Rp {}", order.getTotalPrice());
        log.info(" Total: Rp {}", order.getTotalPrice().multiply(new java.math.BigDecimal(order.getQuantity())));
        log.info("");
        log.info("Your order is being processed.");
        log.info("===========================================");
    }
}