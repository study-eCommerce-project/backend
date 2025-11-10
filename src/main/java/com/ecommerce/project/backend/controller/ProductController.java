package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.dto.ProductDto;
import com.ecommerce.project.backend.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
//배포 시 주소 주의
@CrossOrigin(origins = "http://localhost:3000") // 프론트 연결
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 전체 상품 리스트
    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    // 단일 상품 상세
    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
