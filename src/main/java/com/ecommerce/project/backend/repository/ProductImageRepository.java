package com.ecommerce.project.backend.repository;

import com.ecommerce.project.backend.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct_ProductIdOrderBySortOrderAsc(Long productId);

    // 상품 ID로 관련된 모든 이미지를 삭제하는 메서드
    void deleteAllByProduct_ProductId(Long productId);

    // 이미지 ID로 해당 이미지를 삭제하는 메서드
    void deleteAllByImageIdIn(List<Long> imageIds);

}

