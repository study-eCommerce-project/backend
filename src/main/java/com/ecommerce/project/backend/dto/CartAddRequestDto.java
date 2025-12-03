package com.ecommerce.project.backend.dto;

import lombok.Getter;
import lombok.Setter;

//@Data
@Getter
@Setter
public class CartAddRequestDto {
    private Long productId;   // 상품 ID (필수)
    private String optionValue;
    private Integer quantity; // 수량
    private String optionTitle;

}
