package com.example.shippingservice.consumer;

import com.example.shippingservice.entity.Shipping;
import com.example.shippingservice.repository.ShippingRepository;
import com.example.shippingservice.event.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShippingConsumer {
    private static final Logger log = LoggerFactory.getLogger(ShippingConsumer.class);

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment-success-topic-v5", groupId = "shipping-group-v5")
    public void processShipping(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            log.info("=========================================");
            log.info("🚚 [SHIPPING] Menerima konfirmasi pembayaran untuk Order: {}", event.getOrderId());
            log.info("📦 [SHIPPING] Menyiapkan produk: {}", event.getProductName());
            log.info("✅ [SHIPPING] Order BERHASIL dikirim ke alamat tujuan!");
            log.info("=========================================");

            Shipping shipping = new Shipping();
            shipping.setOrderId(event.getOrderId());
            shipping.setCustomerName(event.getCustomerName());
            shipping.setProductName(event.getProductName());
            shipping.setStatus("SHIPPED");

            shippingRepository.save(shipping);

            kafkaTemplate.send("shipping-success-topic-v5", event.getOrderId());

        } catch (Exception e) {
            log.error("Gagal memproses pesan shipping", e);
        }
    }
}