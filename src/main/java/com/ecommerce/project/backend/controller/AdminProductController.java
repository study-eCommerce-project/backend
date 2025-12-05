package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Category;
import com.ecommerce.project.backend.dto.ProductDto;
import com.ecommerce.project.backend.repository.CategoryRepository;
import com.ecommerce.project.backend.service.AdminProductService;
import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.domain.ProductOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;  // categoryRepository 추가

    // 생성자 주입
    public AdminProductController(AdminProductService adminProductService, CategoryRepository categoryRepository) {
        this.adminProductService = adminProductService;
        this.categoryRepository = categoryRepository;
    }

    /** 상품 등록 */
    @PostMapping("/create")
    public String createProduct(@RequestBody ProductDto productDto) {
        // categoryCode를 받아서 처리
        String categoryCode = productDto.getCategoryCode();

        // category 처리 로직
        Category category = categoryRepository.findByCategoryCode(categoryCode)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));

        // Product 생성 로직
        adminProductService.createProduct(productDto);

        return "상품이 성공적으로 등록되었습니다.";
    }

    /** 상품 수정 */
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductDto productDto) {

        try {
            // categoryCode 처리
            String categoryCode = productDto.getCategoryCode();
            Category category = categoryRepository.findByCategoryCode(categoryCode)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));

            // 상품 업데이트 처리
            Product updatedProduct = adminProductService.updateProduct(productId, productDto);

            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



//    // ------------------------------------------
//    // 🔥 AI 상세페이지 생성 기능
//    // ------------------------------------------
//
//    /** AI 상세페이지 생성 (미리보기용, 지금은 Dummy HTML 반환) */
//    @PostMapping("/{id}/ai-generate")
//    public ResponseEntity<?> generateAiDetail(@PathVariable Long id) {
//        String html = adminProductService.generateAiDetailPreview(id);
//        return ResponseEntity.ok(Map.of("aiDetailHtml", html));
//    }
//
//    /** AI가 생성한 상세페이지 HTML 저장 */
//    @PutMapping("/{id}/description")
//    public ResponseEntity<?> updateDescription(
//            @PathVariable Long id,
//            @RequestBody Map<String, String> body
//    ) {
//        String description = body.get("description");
//        adminProductService.updateDescription(id, description);
//        return ResponseEntity.ok("상품 상세 페이지가 저장되었습니다.");
//    }
}
