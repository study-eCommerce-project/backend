package com.ecommerce.project.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;


import java.time.LocalDateTime;

@Entity
@Table(name = "member_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 Member FK 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String name;
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "detail")
    private String detail;

    @Column(name = "zipcode")
    private String zipcode;


    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

