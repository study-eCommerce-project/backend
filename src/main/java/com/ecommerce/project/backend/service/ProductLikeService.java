package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.config.MusinsaConfig;
import com.ecommerce.project.backend.domain.ProductLike;
import com.ecommerce.project.backend.dto.ProductDto;
import com.ecommerce.project.backend.repository.ProductLikeRepository;
import com.ecommerce.project.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository likeRepository;
    private final ProductRepository productRepository;   // ⭐ 추가
    private final MusinsaConfig musinsaConfig;          // ⭐ 추가

    @Transactional
    public boolean toggleLike(Long memberId, Long productId) {

        boolean exists = likeRepository.existsByMemberIdAndProductId(memberId, productId);

        if (exists) {
            likeRepository.deleteByMemberIdAndProductId(memberId, productId);
            return false; // 좋아요 취소
        }

        ProductLike like = ProductLike.builder()
                .memberId(memberId)
                .productId(productId)
                .build();

        likeRepository.save(like);
        return true; // 좋아요 추가
    }

    public List<ProductLike> getMyLikes(Long memberId) {
        return likeRepository.findByMemberId(memberId);
    }

    /** ⭐ 찜 목록(ProductDto 리스트 반환) */
    public List<ProductDto> getMyLikeProducts(Long memberId) {

        String baseUrl = musinsaConfig.getImageBaseUrl();

        return likeRepository.findByMemberId(memberId)
                .stream()
                .map(like -> productRepository.findById(like.getProductId()))
                .flatMap(Optional::stream)
                .map(product -> ProductDto.fromEntity(product, baseUrl))
                .collect(Collectors.toList());
    }
}
