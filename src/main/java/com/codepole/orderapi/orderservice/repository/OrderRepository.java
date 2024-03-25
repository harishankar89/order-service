package com.codepole.orderapi.orderservice.repository;

import com.codepole.orderapi.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdAndOrderDateBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByUserId(UUID userId);

    List<Order> findByUserId(UUID userId);
}
