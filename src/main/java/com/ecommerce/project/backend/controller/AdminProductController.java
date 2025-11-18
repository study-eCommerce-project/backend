package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final AdminProductService adminProductService;

    /** 상품 등록 */
    @PostMapping
    public String createProduct(@RequestBody com.ecommerce.project.backend.domain.Product product) {
        adminProductService.createProduct(product);
        return "상품이 성공적으로 등록되었습니다.";
    }

    /** 가격 수정 */
    @PutMapping("/{id}/price")
    public String updateSellPrice(@PathVariable Long id, @RequestParam java.math.BigDecimal sellPrice) {
        adminProductService.updateSellPrice(id, sellPrice);
        return "할인가가 성공적으로 수정되었습니다.";
    }

    /** 재고 수정 */
    @PutMapping("/{id}/stock")
    public String updateStock(@PathVariable Long id, @RequestParam int stock) {
        adminProductService.updateStock(id, stock);
        return "재고가 성공적으로 수정되었습니다.";
    }

    /** 상품 삭제 */
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return "상품이 삭제되었습니다.";
    }

    // ------------------------------------------
    // 🔥 AI 상세페이지 생성 기능
    // ------------------------------------------

    /** AI 상세페이지 생성 (미리보기용, 지금은 Dummy HTML 반환) */
    @PostMapping("/{id}/ai-generate")
    public ResponseEntity<?> generateAiDetail(@PathVariable Long id) {

        // Service에서 상품 조회 + 나중에 OpenAI 연결
        String html = adminProductService.generateAiDetailPreview(id);

        return ResponseEntity.ok(Map.of("aiDetailHtml", html));
    }

    /** AI가 생성한 상세페이지 HTML 저장 */
    @PutMapping("/{id}/description")
    public ResponseEntity<?> updateDescription(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String description = body.get("description");
        adminProductService.updateDescription(id, description);

        return ResponseEntity.ok("상품 상세 페이지가 저장되었습니다.");
    }
}
