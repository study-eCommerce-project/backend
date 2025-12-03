package com.ecommerce.project.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    /** 로그인 회원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /** 비회원용 session id */
    @Column(name = "session_id")
    private String sessionId;

    /** 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 옵션 값 저장 */
    @Column(name = "option_value", length = 100)
    private String optionValue; // 옵션 값만 저장 (예: 색상, 크기)


    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // 옵션값을 기반으로 ProductOption을 찾는 메서드
    public ProductOption getOption() {
        return findProductOption(optionValue);  // optionValue를 기준으로 ProductOption을 찾음
    }

    // optionValue를 기반으로 ProductOption을 찾는 로직
    private ProductOption findProductOption(String optionValue) {
        // product와 optionValue로 ProductOption을 조회하는 로직
        return product.getProductOptions().stream()
                .filter(option -> option.getOptionValue().equals(optionValue))
                .findFirst()
                .orElse(null);  // 없으면 null 반환
    }

    public String getOptionTitle() {
        ProductOption option = getOption();  // optionValue를 기준으로 ProductOption을 찾음
        return option != null ? option.getOptionTitle() : null;  // optionTitle을 반환
    }

}


