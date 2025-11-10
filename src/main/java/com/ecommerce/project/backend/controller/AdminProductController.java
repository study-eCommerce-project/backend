package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.service.AdminProductService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

// 관리자 전용: 상품 등록, 가격/재고 수정, 삭제

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    // 상품 등록
    @PostMapping
    public String createProduct(@RequestBody Product product) {
        adminProductService.createProduct(product);
        return "상품이 성공적으로 등록되었습니다.";
    }

    // 할인가 수정
    @PutMapping("/{id}/price")
    public String updateSellPrice(@PathVariable Long id, @RequestParam BigDecimal sellPrice) {
        adminProductService.updateSellPrice(id, sellPrice);
        return "할인가가 성공적으로 수정되었습니다.";
    }

    // 재고 직접 수정
    @PutMapping("/{id}/stock")
    public String updateStock(@PathVariable Long id, @RequestParam int stock) {
        adminProductService.updateStock(id, stock);
        return "재고가 성공적으로 수정되었습니다.";
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        // 단순 delete 예시
        adminProductService.deleteProduct(id);
        return "상품이 삭제되었습니다.";
    }
}
