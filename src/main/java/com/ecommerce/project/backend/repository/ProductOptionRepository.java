package com.ecommerce.project.backend.repository;

import com.ecommerce.project.backend.domain.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    // 예: 노출 중인 옵션만 조회
    List<ProductOption> findByIsShow(boolean isShow);

    // 상품 ID로 해당 옵션 목록 조회
    List<ProductOption> findByProduct_ProductId(Long productId);

    @Query("SELECT COALESCE(SUM(o.stock), 0) FROM ProductOption o WHERE o.product.productId = :productId")
    int sumStockByProductId(@Param("productId") Long productId);


}