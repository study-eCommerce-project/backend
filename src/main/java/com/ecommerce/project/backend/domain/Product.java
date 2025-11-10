package com.ecommerce.project.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "test_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String productName; // 상품명

    @Column(columnDefinition = "TEXT")
    private String description; // 상품 설명 (AI 설명 가능)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consumerPrice; // 정상가

    @Column(precision = 10, scale = 2)
    private BigDecimal sellPrice; // 판매가

    @Column(nullable = false)
    private Integer stock; // 재고 수량

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isOption; // 옵션 존재 여부

    @Column(length = 255)
    private String mainImg; // 대표 이미지

    @Column(length = 255)
    private String thumbnailUrl; // 썸네일 이미지

    @Column(insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt; // 등록일

    @Column(insertable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt; // 수정일

    // product_image (1:N 매핑)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();


    // 재고 차감
    public void decreaseStock(int quantity) {
        if (this.stock - quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

    // 재고 수정 (관리자용)
    public void updateStock(int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("재고는 음수가 될 수 없습니다.");
        }
        this.stock = newStock;
    }

    // 판매가 수정 (관리자용)
    public void updateSellPrice(BigDecimal newSellPrice) {
        if (newSellPrice != null && newSellPrice.compareTo(this.consumerPrice) > 0) {
            throw new IllegalArgumentException("판매가는 정상가보다 높을 수 없습니다.");
        }
        this.sellPrice = newSellPrice;
    }
}
