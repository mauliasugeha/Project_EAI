package com.example.foodDelivery.controller;

import com.example.foodDelivery.consumer.ShippingConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @GetMapping("/status")
    public String getShippingStatus() {
        return ShippingConsumer.getLastShippingStatus();
    }
}