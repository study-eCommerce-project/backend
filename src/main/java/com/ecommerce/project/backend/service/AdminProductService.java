package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

//관리자 전용 — 가격 변경 / 재고 관리 / 상품 등록

@Service
public class AdminProductService {
    private final ProductRepository productRepository;

    public AdminProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 할인가 변경 (관리자 전용)
    public void updateSellPrice(Long productId, BigDecimal newSellPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        product.updateSellPrice(newSellPrice); // ← 도메인 로직 호출
        productRepository.save(product);
    }

    // 재고 직접 조정 (관리자용)
    public void updateStock(Long productId, int newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        product.updateStock(newStock);
        productRepository.save(product);
    }

    // 새 상품 등록
    public void createProduct(Product product) {
        productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
