package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Category;
import com.ecommerce.project.backend.dto.AiProductRequestDto;
import com.ecommerce.project.backend.dto.ProductDto;
import com.ecommerce.project.backend.repository.CategoryRepository;
import com.ecommerce.project.backend.service.AdminProductService;
import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.service.AiDescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;  // categoryRepository 추가\
    private final AiDescriptionService aiDescriptionService;

    // 생성자 주입
    public AdminProductController(
            AdminProductService adminProductService,
            CategoryRepository categoryRepository,
            AiDescriptionService aiDescriptionService
        ){
        this.adminProductService = adminProductService;
        this.categoryRepository = categoryRepository;
        this.aiDescriptionService = aiDescriptionService;
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

    /** 관리자 페이지에서 AI 생성 호출 */
    @PostMapping("/generate-description")
    public ResponseEntity<?> generateDescription(@RequestBody AiProductRequestDto req) {
        AiDescriptionService.Response aiRes = aiDescriptionService.generateDescription(req);
        return ResponseEntity.ok(aiRes);
    }

    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.ok("deleted");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAdminProductList() {
        return ResponseEntity.ok(adminProductService.getAdminProductList());
    }
}
