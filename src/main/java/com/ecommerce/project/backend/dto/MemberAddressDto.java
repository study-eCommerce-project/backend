package com.ecommerce.project.backend.dto;

import com.ecommerce.project.backend.domain.MemberAddress;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAddressDto {

    private Long id;
    private String name;
    private String phone;
    private String address;
    private String detail;
    private Boolean isDefault;
    private String zipcode;

    public static MemberAddressDto fromEntity(MemberAddress addr) {
        return MemberAddressDto.builder()
                .id(addr.getId())
                .name(addr.getName())
                .phone(addr.getPhone())
                .address(addr.getAddress())
                .detail(addr.getDetail())
                .zipcode(addr.getZipcode())
                .isDefault(addr.getIsDefault())
                .build();
    }


}
