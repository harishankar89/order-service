package com.codepole.orderapi.orderservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TestResponse {
    private UUID userId;
    private UUID orderId;
}
