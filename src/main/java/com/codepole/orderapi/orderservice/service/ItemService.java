package com.codepole.orderapi.orderservice.service;

import com.codepole.orderapi.orderservice.config.DiscountConfig;
import com.codepole.orderapi.orderservice.model.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final DiscountConfig discountConfig;

    public ItemService(DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    public Item calculatePriceWithDiscount(Item item) {
        double quantity = item.getQuantity();
        double unitPrice = item.getUnitPrice();
        item.setOriginalUnitPrice(unitPrice);
        double discountRate;

        if (quantity >= discountConfig.getThresholdMin() && quantity <= discountConfig.getThresholdMax()) {
            discountRate = discountConfig.getRateMin();
        } else if (quantity > discountConfig.getThresholdMax()) {
            discountRate = discountConfig.getRateMax();
        } else {
            discountRate = 0;
        }
        item.setUnitPrice(unitPrice * (1 - discountRate));
        return item;
    }
}