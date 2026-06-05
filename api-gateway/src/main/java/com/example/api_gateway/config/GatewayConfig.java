package com.example.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 1. Rute untuk Customer (port 8080)
                .route("customer_route", r -> r.path("/customers", "/customers/**")
                        .uri("http://localhost:8080"))

                // 2. Rute untuk Order dan Produk (port 8080)
                .route("order_route", r -> r.path("/api/orders/**")
                        .uri("http://localhost:8080"))
                .route("product_route", r -> r.path("/api/products/**")
                        .uri("http://localhost:8080"))

                // 3. Rute untuk Ecommerce (port 8081)
                .route("ecommerce_route", r -> r.path("/payment/**", "/shipping/**")
                        .uri("http://localhost:8081"))

                .build();
    }
}