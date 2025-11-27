package com.ecommerce.project.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {
    private String receiverName;
    private String receiverPhone;
    private String address;
    private String addressDetail;
    private String zipcode;

    private List<Long> cartIds; // 결제할 카트ID 목록
}

