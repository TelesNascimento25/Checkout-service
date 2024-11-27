package com.qikserve.checkout.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private String id;
    private String name;
    private int price;
}