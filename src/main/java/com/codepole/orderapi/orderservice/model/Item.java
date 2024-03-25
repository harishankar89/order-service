package com.codepole.orderapi.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "items")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column
    @NotNull
    private UUID itemId;
    /*@ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;*/
    @Column
    @NotBlank
    private String itemName;
    @Column
    @Min(0)
    private double unitPrice;
    @Column
    private double originalUnitPrice;
    @Column
    @Min(1)
    private double quantity;

}