package com.ecommerce.project.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String phone;

    private String address;
    private String addressDetail;

    @Builder.Default
    private String role = "USER";

    // 포인트 필드
    @Builder.Default
    @Column(nullable = false)
    private Integer point = 0;
}

