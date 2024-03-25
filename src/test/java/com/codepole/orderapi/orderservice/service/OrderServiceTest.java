package com.codepole.orderapi.orderservice.service;

import com.codepole.orderapi.orderservice.model.Item;
import com.codepole.orderapi.orderservice.model.Order;
import com.codepole.orderapi.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        Item item = new Item();
        item.setItemId(UUID.randomUUID());
        item.setItemName("Test Item");
        item.setQuantity(5);
        item.setUnitPrice(10.0);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(UUID.randomUUID());
        order.setItems(Collections.singletonList(item));
        order.setOrderDate(LocalDateTime.now());

        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(order);
        when(itemService.calculatePriceWithDiscount(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultOrderId = UUID.fromString(orderService.createOrder(Collections.singletonList(item), order.getUserId()));

        assertNotNull(resultOrderId);
        assertEquals(order.getId(), resultOrderId);
    }

    @Test
    void testGetTotalPrice() {
        UUID userId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Item item = new Item();
        item.setItemId(UUID.randomUUID());
        item.setItemName("Test Item");
        item.setQuantity(5);
        item.setUnitPrice(10.0);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(userId);
        order.setItems(Collections.singletonList(item));
        order.setOrderDate(LocalDateTime.now());

        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.existsByUserId(userId)).thenReturn(true);
        when(orderRepository.findByUserIdAndOrderDateBetween(userId, startDate, endDate)).thenReturn(orders);

        double totalPrice = orderService.getTotalPrice(startDate, endDate, userId);

        assertEquals(item.getUnitPrice() * item.getQuantity(), totalPrice);
    }
    @Test
    void testGetAllOrders() {
        UUID userId = UUID.randomUUID();

        Item item = new Item();
        item.setItemId(UUID.randomUUID());
        item.setItemName("Test Item");
        item.setQuantity(5);
        item.setUnitPrice(10.0);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(userId);
        order.setItems(Collections.singletonList(item));
        order.setOrderDate(LocalDateTime.now());

        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        List<Order> resultOrders = orderService.getAllOrders(userId);

        assertEquals(orders, resultOrders);
    }
}
