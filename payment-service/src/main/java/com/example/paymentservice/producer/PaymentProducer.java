package com.example.paymentservice.producer;

import com.example.paymentservice.event.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentProducer {
    private static final Logger log = LoggerFactory.getLogger(PaymentProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentSuccess(OrderEvent order) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("payment-success-topic-v5", jsonMessage);
//            log.info("PAYMENT-SERVICE: Mengirim Sukses ke Kafka: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Gagal convert JSON: {}", e.getMessage());
        }
    }

    public void sendPaymentFailed(OrderEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-failed-topic-v5", jsonMessage);
//            log.info("📢 [PRODUCER] Mengirim event Payment Failed");
        } catch (Exception e) {
            log.error("Gagal kirim: {}", e.getMessage());
        }
    }
}