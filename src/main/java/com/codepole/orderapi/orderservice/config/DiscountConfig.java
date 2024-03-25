package com.codepole.orderapi.orderservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "discount")
@Getter
@Setter
public class DiscountConfig {
    private int thresholdMin;
    private double rateMin;
    private int thresholdMax;
    private double rateMax;
}
