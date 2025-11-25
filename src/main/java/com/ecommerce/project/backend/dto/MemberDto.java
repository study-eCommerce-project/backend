package com.ecommerce.project.backend.dto;

import lombok.*;
import com.ecommerce.project.backend.domain.Member;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private Long id;
    private String email;
    private String password; // 회원가입 시만 사용
    private String name;
    private String phone;
    private String address;
    private String addressDetail;
    private String role;

    // Entity → DTO
    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(null) // 비밀번호는 절대 노출 X
                .name(member.getName())
                .phone(member.getPhone())
                .address(member.getAddress())
                .addressDetail(member.getAddressDetail())
                .role(member.getRole())
                .build();
    }

    // DTO → Entity
    public Member toEntity() {
        return Member.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password) // 회원가입 시만 입력됨
                .name(this.name)
                .phone(this.phone)
                .address(this.address)
                .addressDetail(this.addressDetail)
                .role(this.role != null ? this.role : "USER") // 기본값 USER
                .build();
    }
}
