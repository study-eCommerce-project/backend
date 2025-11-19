package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.domain.*;
import com.ecommerce.project.backend.dto.CartAddRequestDto;
import com.ecommerce.project.backend.dto.CartItemDto;
import com.ecommerce.project.backend.dto.CartResponseDto;
import com.ecommerce.project.backend.dto.OptionDto;
import com.ecommerce.project.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final ProductService productService;

    /** 장바구니 담기 */
    public void addToCart(Long memberId, CartAddRequestDto req) {


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        boolean isOptionProduct = product.getIsOption();

        if (isOptionProduct) {
            ProductOption option = optionRepository.findById(req.getOptionId())
                    .orElseThrow(() -> new RuntimeException("옵션 없음"));

            // 재고 체크
            if (option.getStock() < req.getQuantity()) {
                throw new IllegalArgumentException("재고 부족");
            }

            // 중복 cart
            Optional<Cart> existing = cartRepository
                    .findByMember_IdAndProduct_ProductIdAndOption_OptionId(
                            memberId, req.getProductId(), req.getOptionId());

            if (existing.isPresent()) {
                Cart cart = existing.get();
                int newQty = cart.getQuantity() + req.getQuantity();
                if (option.getStock() < newQty) throw new RuntimeException("재고 부족");
                cart.setQuantity(newQty);
                return;
            }

            cartRepository.save(Cart.builder()
                    .member(member)
                    .product(product)
                    .option(option)
                    .quantity(req.getQuantity())
                    .build());
        }

        else {
            // 단일상품
            if (product.getStock() < req.getQuantity()) throw new RuntimeException("재고 없음");

            Optional<Cart> existing = cartRepository
                    .findByMember_IdAndProduct_ProductIdAndOptionIsNull(
                            memberId, req.getProductId());

            if (existing.isPresent()) {
                Cart cart = existing.get();
                int newQty = cart.getQuantity() + req.getQuantity();
                if (product.getStock() < newQty) throw new RuntimeException("재고 부족");
                cart.setQuantity(newQty);
                return;
            }

            cartRepository.save(
                    Cart.builder()
                            .member(member)
                            .product(product)
                            .option(null)
                            .quantity(req.getQuantity())
                            .build()
            );
        }
    }

    /** 장바구니 조회 */
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long memberId) {

        List<Cart> carts = cartRepository.findByMember_Id(memberId);

        List<CartItemDto> items = carts.stream().map(cart -> {
            Product p = cart.getProduct();
            ProductOption o = cart.getOption();

            boolean soldOut = (o == null) ? p.getStock() <= 0 : o.getStock() <= 0;

            return CartItemDto.builder()
                    .cartId(cart.getCartId())
                    .productId(p.getProductId())
                    .productName(p.getProductName())
                    .thumbnail(p.getMainImg())
                    .quantity(cart.getQuantity())
                    .price(p.getSellPrice().intValue())
                    .stock(o == null ? p.getStock() : o.getStock())
                    .soldOut(soldOut)
                    .option(o == null ? null : OptionDto.builder()
                            .optionId(o.getOptionId())
                            .optionType(o.getOptionType())
                            .optionTitle(o.getOptionTitle())
                            .optionValue(o.getOptionValue())
                            .colorCode(o.getColorCode())
                            .build())
                    .build();
        }).toList();

        int totalPrice = items.stream()
                .mapToInt(i -> i.getPrice() * i.getQuantity())
                .sum();

        int totalQty = items.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();

        return CartResponseDto.builder()
                .items(items)
                .totalPrice(totalPrice)
                .totalQuantity(totalQty)
                .build();
    }

    @Transactional
    public void updateQuantity(Long cartId, int quantity) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("장바구니 항목 없음"));

        Product product = cart.getProduct();
        ProductOption option = cart.getOption();

        // 옵션 상품인지 단일 상품인지 구분
        int stock = (option == null) ? product.getStock() : option.getStock();

        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        if (stock < quantity) {
            throw new IllegalArgumentException("재고 부족");
        }

        cart.setQuantity(quantity);
    }

    @Transactional
    public void changeOption(Long memberId, Long cartId, Long newOptionId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("장바구니 항목 없음"));

        // 본인 장바구니인지 검증 (보안)
        if (!cart.getMember().getId().equals(memberId)) {
            throw new RuntimeException("다른 사용자의 장바구니 수정 불가");
        }

        Product product = cart.getProduct();

        // 상품이 옵션 상품인지 확인
        if (!product.getIsOption()) {
            throw new RuntimeException("단일상품은 옵션 변경 불가");
        }

        // 변경할 옵션 조회
        ProductOption newOption = optionRepository.findById(newOptionId)
                .orElseThrow(() -> new RuntimeException("옵션 없음"));

        // 재고 확인
        if (newOption.getStock() < cart.getQuantity()) {
            throw new RuntimeException("재고 부족");
        }

        // 옵션 변경
        cart.setOption(newOption);
    }

    @Transactional
    public void delete(Long cartId, Long memberId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("장바구니 항목 없음"));

        // 본인 장바구니인지 검증
        if (!cart.getMember().getId().equals(memberId)) {
            throw new RuntimeException("다른 사용자의 장바구니 삭제 불가");
        }

        cartRepository.delete(cart);
    }




}



