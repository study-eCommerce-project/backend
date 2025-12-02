package com.ecommerce.project.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private Long orderItemId;
    private Long orderId;
    private Long productId;

    private String productName;
    private Long optionId;       // 옵션 ID
    private String optionValue;  // 옵션 이름

    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    private String mainImg;      // 🔥 주문 시 복사된 상품 이미지

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
