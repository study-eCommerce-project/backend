package com.ecommerce.project.backend.dto;

import com.ecommerce.project.backend.domain.Product;
import com.ecommerce.project.backend.domain.ProductOption;
import com.ecommerce.project.backend.repository.ProductRepository;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionDto {
    private Long optionId;
    private Long productId;
    private String optionTitle;
    private String optionValue;
    private Integer stock;
    private Boolean isShow;  // isShow 추가
    private String colorCode;
    private BigDecimal sellPrice;
    private BigDecimal consumerPrice;
    private String optionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // fromEntity 메서드를 통해 ProductOption을 ProductOptionDto로 변환
    public static ProductOptionDto fromEntity(ProductOption e) {
        return ProductOptionDto.builder()
                .optionId(e.getOptionId())
                .productId(e.getProduct().getProductId())
                .consumerPrice(e.getConsumerPrice())
                .sellPrice(e.getSellPrice())
                .isShow(e.getIsShow())  // getIsShow() 메서드를 호출하여 isShow 값 사용
                .optionTitle(e.getOptionTitle())
                .optionValue(e.getOptionValue())
                .optionType(e.getOptionType())
                .stock(e.getStock())
                .colorCode(e.getColorCode())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }


    public ProductOption toEntity(ProductRepository productRepository) {
        // productId로 Product 객체 찾기
        Product product = productRepository.findById(this.productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + this.productId));

        // ProductOption 객체 생성 후, Product 설정
        ProductOption option = new ProductOption();
        option.setOptionId(this.optionId);
        option.setOptionTitle(this.optionTitle);
        option.setOptionValue(this.optionValue);
        option.setStock(this.stock);
        option.setProduct(product);  // productId로 찾은 Product 객체 연결
        option.setSellPrice(this.sellPrice);
        option.setConsumerPrice(this.consumerPrice);
        option.setIsShow(this.isShow);
        option.setColorCode(this.colorCode);

        return option;
    }
}