package com.codepole.orderapi.orderservice.api;

import com.codepole.orderapi.orderservice.model.Item;
import com.codepole.orderapi.orderservice.model.Order;
import com.codepole.orderapi.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/order")
public class OrderController implements OrderApi {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }


    @Override
    public ResponseEntity<String> createOrder(List<Item> items, UUID userId) {
        return ResponseEntity.ok(service.createOrder(items, userId));
    }

    @Override
    public ResponseEntity<BigDecimal> getTotalPrice(LocalDateTime startDate, LocalDateTime endDate, UUID userId) {
        double totalPrice = service.getTotalPrice(startDate, endDate, userId);
        return ResponseEntity.ok(new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public ResponseEntity<List<Order>> getAllOrders(UUID userId) {
        List<Order> orders = service.getAllOrders(userId);
        return ResponseEntity.ok(orders);
    }
}
