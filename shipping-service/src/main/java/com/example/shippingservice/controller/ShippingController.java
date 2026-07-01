package com.example.shippingservice.controller;

import com.example.shippingservice.consumer.ShippingConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.shippingservice.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private ShippingRepository repository;

    @GetMapping("/status/{orderId}")
    public String getShippingStatus(@PathVariable String orderId) {
        return repository.findById(orderId)
                .map(s -> "Status Pengiriman: " + s.getStatus())
                .orElse("BELUM DIKIRIM / ORDER TIDAK DITEMUKAN");
    }
}
