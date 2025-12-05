package com.ecommerce.project.backend.dto;

import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.domain.ProductImage;

public class ProductImageDto {
    private String imageUrl;   // 이미지 URL
    private int sortOrder;     // 이미지 순서
    private long productId;    // 상품 ID

    // Getter, Setter 및 생성자 추가
    public ProductImageDto(String imageUrl, int sortOrder, long productId) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }


    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

// ProductImageDto에서 ProductImage 엔티티로 변환하는 메서드 (빌더 패턴 사용)
    public ProductImage toEntity(Product product) {
        return ProductImage.builder()
                .imageUrl(this.imageUrl)      // DTO에서 받은 imageUrl을 사용
                .sortOrder(this.sortOrder)    // DTO에서 받은 sortOrder를 사용
                .product(product)             // 전달받은 product 객체를 사용
                .build();                     // 빌더로 ProductImage 객체 생성
    }

}
