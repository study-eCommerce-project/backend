package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;

    /** 상품 등록 */
    public Product createProduct(Product product) {
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("재고는 음수가 될 수 없습니다.");
        }
        if (product.getSellPrice().compareTo(product.getConsumerPrice()) > 0) {
            throw new IllegalArgumentException("판매가는 소비자가보다 높을 수 없습니다.");
        }

        return productRepository.save(product);
    }

    /** 할인가(판매가) 수정 */
    public void updateSellPrice(Long productId, BigDecimal newSellPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        product.updateSellPrice(newSellPrice); // 엔티티 도메인 메서드 활용
        productRepository.save(product);
    }

    /** 재고 수정 */
    public void updateStock(Long productId, int newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        product.updateStock(newStock); // 엔티티 도메인 메서드 활용
        productRepository.save(product);
    }

    /** 상품 삭제 */
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("삭제할 상품이 존재하지 않습니다. ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    // ---------------------------------------------------------------------
    // AI 상세페이지 기능
    // ---------------------------------------------------------------------

    /** AI 상세페이지 생성 (test용) */
    public String generateAiDetailPreview(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        // 지금은 더미 HTML → 나중에 OpenAI HTML로 변경될 부분
        return """
                <h2>AI 자동 생성 상세페이지</h2>
                <p>상품명: %s</p>
                <p>여기는 나중에 OpenAI가 생성한 HTML로 대체됩니다.</p>
                """.formatted(product.getProductName());
    }

    /** AI가 생성한 상세 HTML 저장 */
    public void updateDescription(Long productId, String description) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        product.updateDescription(description);
        productRepository.save(product);
    }
}
