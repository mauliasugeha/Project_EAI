package com.example.foodDelivery.controller;

import com.example.foodDelivery.consumer.ShippingConsumer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @GetMapping("/status/{orderId}")
    public String getShippingStatus(
            @PathVariable String orderId
    ) {

        return ShippingConsumer
                .getShippingStatus(orderId);
    }
}