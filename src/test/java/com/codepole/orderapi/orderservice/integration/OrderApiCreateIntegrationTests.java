package com.codepole.orderapi.orderservice.integration;

import com.codepole.orderapi.orderservice.OrderServiceApplication;
import com.codepole.orderapi.orderservice.model.Item;
import com.codepole.orderapi.orderservice.model.Order;
import com.codepole.orderapi.orderservice.model.TestResponse;
import com.codepole.orderapi.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderApiCreateIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;


    private UUID userID = UUID.randomUUID();
    @Test
    @Commit
    @org.junit.jupiter.api.Order(1)
    public void testCreateOrderEndpointSuccess() throws Exception {
        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .itemName("Integration Test Item")
                .quantity(2)
                .unitPrice(20.0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-id", userID.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(new ObjectMapper().writeValueAsString(Collections.singletonList(item)), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/order/create", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UUID orderId = UUID.fromString(response.getBody());

        Order order = orderRepository.findById(orderId).orElse(null);

        assertNotNull(order);
        assertEquals(item.getItemId(), order.getItems().get(0).getItemId());
        assertEquals(item.getItemName(), ((Order) order).getItems().get(0).getItemName());
        assertEquals(item.getQuantity(), order.getItems().get(0).getQuantity());
        assertEquals(item.getUnitPrice(), order.getItems().get(0).getUnitPrice());
    }

    @Test
    public void testCreateOrderConstraintViolation() throws Exception {
        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .itemName("Integration Test Item")
                .quantity(-2)
                .unitPrice(20.0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-id", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(new ObjectMapper().writeValueAsString(Collections.singletonList(item)), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/order/create", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testCreateOrderMsgNotReadableException() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-id", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>("\"[{\\\"itemId\\\": \\\"123e4567-e89b-12d3-a456-426614174000\\\", \\\"itemName\\\": \\\"Item 1\\\", \\\"unitPrice\\\": null, \\\"quantity\\\": 5, \\\"quantity1\\\": 5}]\"", headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/order/create", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return date.format(formatter);
    }
}
