package com.codepole.orderapi.orderservice.integration;

import com.codepole.orderapi.orderservice.OrderServiceApplication;
import com.codepole.orderapi.orderservice.model.Item;
import com.codepole.orderapi.orderservice.model.Order;
import com.codepole.orderapi.orderservice.model.TestResponse;
import com.codepole.orderapi.orderservice.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public class OrderApiGetIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    private TestResponse testResponse = new TestResponse();

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Commit
    public void testCreateOrder() {
        UUID userID = UUID.randomUUID();
        Item item = Item.builder()
                .itemId(UUID.randomUUID())
                .itemName("Integration Test Item")
                .quantity(2)
                .unitPrice(100.0)
                .build();
        Order order = Order.builder()
                .userId(userID)
                .items(Collections.singletonList(item))
                .orderDate(LocalDateTime.now())
                .build();
        UUID orderId = orderRepository.saveAndFlush(order).getId();
        orderRepository.flush();
        testResponse.setUserId(userID);
        testResponse.setOrderId(orderId);
        entityManager.clear(); // Clear persistence context to force re-fetch from DB
        Order persistedOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(persistedOrder);
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void testGetTotalPriceSuccessScenario() throws Exception {
        UUID orderId = testResponse.getOrderId();
        String startDate = "2024-01-01T00:00:00";
        String endDate = formatDate(LocalDateTime.now());
        entityManager.clear(); // Clear persistence context to force re-fetch from DB
        Order order = orderRepository.findById(orderId).orElse(null);
        //Thread.sleep(21 * 1000);
        MockHttpServletResponse response2 = mockMvc.perform(get("/order/getTotalPrice?startDate=" + startDate + "&endDate=" + endDate)
                .header("user-id", testResponse.getUserId())).andExpect(status().isOk()).andReturn().getResponse();
        /*Order order1 = orderRepository.findById(orderId).orElse(null);
        assertEquals("200.00", response2.getContentAsString());*/
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testGetTotalPriceNotFound() throws Exception {
        UUID userID = UUID.randomUUID();
        String startDate = "2022-01-01T00:00:00";
        String endDate = "2022-12-31T23:59:59";
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-id", userID.toString());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/order/getTotalPrice?startDate=" + startDate + "&endDate=" + endDate,
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testGetTotalPriceMethodArgumentTypeMismatchException() throws Exception {
        //UUID userID = UUID.randomUUID();
        String startDate = "2022-01-01T00:00:00";
        String endDate = "2022-12-31T23:59:59";
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-id", "InvalidUserID");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/order/getTotalPrice?startDate=" + startDate + "&endDate=" + endDate,
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testGetAllOrders() throws Exception {
        UUID orderId = testResponse.getOrderId();
        entityManager.clear(); // Clear persistence context to force re-fetch from DB
        Order order = orderRepository.findById(orderId).orElse(null);
        //Thread.sleep(21 * 1000);
        MockHttpServletResponse response = mockMvc.perform(get("/order/getAllOrders")
                .header("user-id", testResponse.getUserId())).andExpect(status().isOk()).andReturn().getResponse();
        assertNotNull(response.getContentAsString());
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return date.format(formatter);
    }
}
