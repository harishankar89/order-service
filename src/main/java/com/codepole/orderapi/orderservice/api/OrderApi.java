package com.codepole.orderapi.orderservice.api;

import com.codepole.orderapi.orderservice.model.Item;
import com.codepole.orderapi.orderservice.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderApi {

    @PostMapping("/create")
    @Operation(summary = "Create Order for the given list of items")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Item.class),
                    examples = @ExampleObject(value = "[{\"itemId\": \"123e4567-e89b-12d3-a456-426614174000\", \"itemName\": \"Item 1\", \"unitPrice\": 10.0, \"quantity\": 5}]")
            )
    )
    @Parameter(name = "user-id", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.HEADER)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = {@Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "123e4567-e89b-12d3-a456-426614174001"),
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Input supplied", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Contact server admin", content = @Content)})
    ResponseEntity<String> createOrder(@Valid @RequestBody List<@Valid Item> items, @NotNull @RequestHeader("user-id") UUID userId);

    @GetMapping("/getTotalPrice")
    @Operation(summary = "Get the total price of all orders in the given date range of a given user")
    @Parameter(name = "startDate", required = true, example = "2024-03-24T23:59:59", in = ParameterIn.QUERY)
    @Parameter(name = "endDate", required = true, example = "2024-03-25T23:59:59", in = ParameterIn.QUERY)
    @Parameter(name = "user-id", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.HEADER)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total price retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "100.0}"),
                            schema = @Schema(implementation = BigDecimal.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input supplied", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact server admin", content = @Content)})
    ResponseEntity<BigDecimal> getTotalPrice(
            @NotNull @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SS") LocalDateTime startDate,
            @NotNull @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SS") LocalDateTime endDate,
            @NotNull @RequestHeader("user-id") UUID userId);

    @GetMapping("/getAllOrders")
    @Operation(summary = "Get all orders of a given user")
    @Parameter(name = "user-id", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.HEADER)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"123e4567-e89b-12d3-a456-426614174000\", \"items\": [{\"itemId\": \"123e4567-e89b-12d3-a456-426614174000\", \"itemName\": \"Item 1\", \"unitPrice\": 10.0, \"quantity\": 5}], \"orderDate\": \"2022-01-01T00:00:00\"}]"),
                            schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input supplied", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact server admin", content = @Content)})
    ResponseEntity<List<Order>> getAllOrders(@NotNull @RequestHeader("user-id") UUID userId);
}
