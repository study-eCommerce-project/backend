package com.ecommerce.project.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_image")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false, insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(nullable = false, insertable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
