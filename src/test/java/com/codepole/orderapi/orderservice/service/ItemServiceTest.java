package com.codepole.orderapi.orderservice.service;
import com.codepole.orderapi.orderservice.config.DiscountConfig;
import com.codepole.orderapi.orderservice.model.Item;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemServiceTest {

    private final DiscountConfig discountConfig = new DiscountConfig();
    private final ItemService itemService = new ItemService(discountConfig);

    @Test
    void calculatePriceWithNoDiscount() {

        discountConfig.setThresholdMin(5);
        discountConfig.setRateMin(0.10);

        Item item = Item.builder()
                .quantity(4)
                .unitPrice(100.0)
                .build();

        Item result = itemService.calculatePriceWithDiscount(item);

        assertEquals(100.0, result.getUnitPrice());
    }

    @Test
    void calculatePriceWithN10Discount() {
        discountConfig.setThresholdMin(5);
        discountConfig.setRateMin(0.10);
        discountConfig.setThresholdMax(10);
        discountConfig.setRateMax(0.15);
        Item item = Item.builder()
                .quantity(5)
                .unitPrice(100.0)
                .build();
        Item result = itemService.calculatePriceWithDiscount(item);

        assertEquals(90.0, result.getUnitPrice());
    }
    @Test
    void calculatePriceWithN15Discount() {
        discountConfig.setThresholdMin(5);
        discountConfig.setRateMin(0.10);
        discountConfig.setThresholdMax(10);
        discountConfig.setRateMax(0.15);
        Item item = Item.builder()
                .quantity(12)
                .unitPrice(100.0)
                .build();

        Item result = itemService.calculatePriceWithDiscount(item);

        assertEquals(85.0, result.getUnitPrice());
    }

}

