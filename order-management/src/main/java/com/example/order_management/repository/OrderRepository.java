package com.example.order_management.repository;

import com.example.order_management.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // <--- INI WAJIB ADA

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}