package com.ecommerce.project.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentOrderDto {

    private Long orderId;        // 결제 후 verify할 때 필요
    private String orderNumber;  // 포트원 결제창 title 용
    private BigDecimal totalPrice; // 결제할 금액
    private String paymentMethod;  // CARD, KAKAOPAY 등
}
