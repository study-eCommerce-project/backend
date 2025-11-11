package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.dto.ProductDto;
import com.ecommerce.project.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 전체 상품 조회
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> ProductDto.builder()
                        .productId(p.getProductId())
                        .productName(p.getProductName())
                        .description(p.getDescription())
                        .consumerPrice(p.getConsumerPrice().doubleValue())
                        .sellPrice(p.getSellPrice().doubleValue())
                        .stock(p.getStock())
                        .isOption(p.getIsOption())
                        .mainImg(p.getMainImg())
                        .thumbnailUrl(p.getThumbnailUrl())
                        .build())
                .collect(Collectors.toList());
    }

    // 단일 상품 조회
    public ProductDto getProductById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        return ProductDto.builder()
                .productId(p.getProductId())
                .productName(p.getProductName())
                .description(p.getDescription())
                .consumerPrice(p.getConsumerPrice().doubleValue())
                .sellPrice(p.getSellPrice().doubleValue())
                .stock(p.getStock())
                .isOption(p.getIsOption())
                .mainImg(p.getMainImg())
                .thumbnailUrl(p.getThumbnailUrl())
                .build();
    }
}
