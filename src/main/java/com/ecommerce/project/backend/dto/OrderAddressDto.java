package com.ecommerce.project.backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddressDto {

    private String name;     // 수령자 이름
    private String phone;    // 전화번호
    private String address;  // 기본 주소
    private String detail;   // 상세 주소
}
