package com.ecommerce.project.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDto {
    private Long productId;
    private String productName;
    private String description;
    private Double consumerPrice;
    private Double sellPrice;
    private Integer stock;
    private Boolean isOption;
    private String mainImg;
    private String thumbnailUrl;
}