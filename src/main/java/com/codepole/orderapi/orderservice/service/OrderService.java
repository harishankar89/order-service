package com.codepole.orderapi.orderservice.service;

import com.codepole.orderapi.orderservice.exception.OrderServiceException;
import com.codepole.orderapi.orderservice.model.Item;
import com.codepole.orderapi.orderservice.model.Order;
import com.codepole.orderapi.orderservice.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemService itemService;

    public OrderService(OrderRepository orderRepository, ItemService itemService) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
    }

    @CacheEvict(value = {"getOrderById", "totalPrice"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createOrder(List<Item> items, UUID userId) {
        Map<UUID, Item> itemMap = createItemMap(items);
        applyDiscounts(itemMap);
        Order order = createAndSaveOrder(itemMap, userId);
        return order.getId().toString();
    }

    private Map<UUID, Item> createItemMap(List<Item> items) {
        return items.stream()
                .collect(Collectors.toMap(
                        Item::getItemId,
                        Function.identity(),
                        (item1, item2) -> {
                            item1.setQuantity(item1.getQuantity() + item2.getQuantity());
                            return item1;
                        }
                ));
    }

    private void applyDiscounts(Map<UUID, Item> itemMap) {
        itemMap.values().forEach(itemService::calculatePriceWithDiscount);
    }

    private Order createAndSaveOrder(Map<UUID, Item> itemMap, UUID userId) {
        Order order = Order.builder()
                .userId(userId)
                .items((new ArrayList<>(itemMap.values())))
                .orderDate(LocalDateTime.now())
                .build();
        return orderRepository.saveAndFlush(order);
    }

    @Cacheable(value = "totalPrice", key = "T(String).format('%s-%s-%s', #userId, #startDate, #endDate)")
    public double getTotalPrice(LocalDateTime startDate, LocalDateTime endDate, UUID userId) {
        if (!orderRepository.existsByUserId(userId)) {
            throw new OrderServiceException("No orders found for user: " + userId);
        }
        List<Order> orders = orderRepository.findByUserIdAndOrderDateBetween(userId, startDate, endDate);
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }

    @Cacheable(value = "getOrderById", key = "#userId")
    public List<Order> getAllOrders(UUID userId) {
        return orderRepository.findByUserId(userId);
    }
}
