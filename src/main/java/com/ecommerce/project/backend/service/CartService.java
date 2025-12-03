package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.config.MusinsaConfig;
import com.ecommerce.project.backend.domain.*;
import com.ecommerce.project.backend.dto.*;
import com.ecommerce.project.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MusinsaConfig musinsaConfig;

    // 장바구니 조회 (CartAddResponseDto 반환)
    public CartAddResponseDto getCartResponse(Long memberId) {
        // 1. memberId로 카트 목록을 조회
        List<Cart> carts = cartRepository.findByMember_Id(memberId);

        // 2. 카트 항목들을 DTO로 변환
        List<CartItemDto> cartItemDtos = carts.stream()
                .map(cart -> CartItemDto.builder()
                        .cartId(cart.getCartId())
                        .productId(cart.getProduct().getProductId())
                        .productName(cart.getProduct().getProductName())
                        .thumbnail(cart.getProduct().getMainImg())
                        .quantity(cart.getQuantity())
                        .price(cart.getProduct().getSellPrice().intValue())
                        .stock(cart.getProduct().getStock())
                        .soldOut(cart.getProduct().getStock() <= 0)
                        .optionValue(cart.getOptionValue())  // 옵션 값 설정
                        .optionTitle(cart.getOptionTitle()) // 옵션 타이틀 설정
                        .build())
                .collect(Collectors.toList());

        // 3. 총 가격과 총 수량 계산
        Integer totalPrice = cartItemDtos.stream().mapToInt(CartItemDto::getPrice).sum();
        Integer totalQuantity = cartItemDtos.stream().mapToInt(CartItemDto::getQuantity).sum();

        // 4. DTO로 응답 생성
        return CartAddResponseDto.builder()
                .items(cartItemDtos)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .build();
    }
    /** -------------------------
     * 장바구니 담기
     * ------------------------- */
    @Transactional
    public CartAddResponseDto addToCart(Long memberId, CartAddRequestDto req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        String optionValue = req.getOptionValue();  // 옵션 값
        boolean isOptionProduct = product.getIsOption();  // 옵션 상품 여부
        String optionTitle = "";  // 옵션 타이틀 기본값 빈 문자열

        List<CartItemDto> cartItemDtos = new ArrayList<>();

        if (isOptionProduct) {
            if (optionValue == null || optionValue.isEmpty()) {
                throw new IllegalArgumentException("옵션 값이 필요합니다.");
            }

            // ProductOption에서 optionValue를 기준으로 optionTitle을 가져옴
            ProductOption productOption = product.getProductOptions().stream()
                    .filter(option -> option.getOptionValue().equals(optionValue))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 옵션입니다."));

            optionTitle = productOption.getOptionTitle();  // DB에서 가져온 optionTitle 사용

            Optional<Cart> existing = cartRepository
                    .findByMember_IdAndProduct_ProductIdAndOptionValue(memberId, req.getProductId(), optionValue);

            if (existing.isPresent()) {
                Cart cart = existing.get();
                int newQty = cart.getQuantity() + req.getQuantity();
                if (newQty > product.getStock()) throw new IllegalArgumentException("재고 부족");
                cart.setQuantity(newQty);
                cartItemDtos.add(buildCartItemDto(cart, optionTitle));
            } else {
                Cart newCart = cartRepository.save(Cart.builder()
                        .member(member)
                        .product(product)
                        .optionValue(optionValue) // optionValue를 그대로 사용
                        .quantity(req.getQuantity())
                        .build());
                cartItemDtos.add(buildCartItemDto(newCart, optionTitle));
            }
        } else {
            if (product.getStock() < req.getQuantity())
                throw new IllegalArgumentException("재고 부족");

            Optional<Cart> existing = cartRepository
                    .findByMember_IdAndProduct_ProductIdAndOptionValueIsNull(memberId, req.getProductId());

            if (existing.isPresent()) {
                Cart cart = existing.get();
                int newQty = cart.getQuantity() + req.getQuantity();
                if (newQty > product.getStock()) throw new IllegalArgumentException("재고 부족");
                cart.setQuantity(newQty);
                cartItemDtos.add(buildCartItemDto(cart, "")); // 옵션이 없으면 optionTitle은 빈 문자열
            } else {
                Cart newCart = cartRepository.save(Cart.builder()
                        .member(member)
                        .product(product)
                        .optionValue("")  // 빈 문자열로 저장 (옵션 없는 상품)
                        .quantity(req.getQuantity())
                        .build());
                cartItemDtos.add(buildCartItemDto(newCart, "")); // 옵션이 없으면 빈 문자열
            }
        }

        int totalPrice = cartItemDtos.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
        int totalQuantity = cartItemDtos.stream().mapToInt(CartItemDto::getQuantity).sum();

        return CartAddResponseDto.builder()
                .items(cartItemDtos)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .build();
    }

    private CartItemDto buildCartItemDto(Cart cart, String optionTitle) {
        Product product = cart.getProduct();
        // 빈 문자열로 설정된 경우, optionTitle은 빈 문자열로 설정
        String finalOptionTitle = (optionTitle == null || optionTitle.isEmpty()) ? "" : optionTitle;
        return CartItemDto.builder()
                .cartId(cart.getCartId())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .thumbnail(product.getMainImg())
                .quantity(cart.getQuantity())
                .price(product.getSellPrice().intValue())
                .stock(product.getStock())
                .soldOut(product.getStock() <= 0)
                .optionValue(cart.getOptionValue()) // optionValue를 그대로 사용
                .optionTitle(finalOptionTitle)  // DB에서 가져온 optionTitle을 그대로 사용
                .build();
    }


    /** -------------------------
     * 장바구니 조회
     * ------------------------- */
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long memberId) {

        // 장바구니 항목 조회
        List<Cart> carts = cartRepository.findByMember_Id(memberId);
        String baseUrl = musinsaConfig.getImageBaseUrl();

        // 장바구니 아이템 정보 생성
        List<CartItemDto> items = carts.stream().map(cart -> {

            Product p = cart.getProduct();
            String optionValue = cart.getOptionValue();  // 옵션 값만 사용 (ProductOption 대신)

            // optionValue에 맞는 optionTitle을 ProductOption에서 찾아오기
            String optionTitle = null;
            if (optionValue != null && !optionValue.isEmpty()) {
                // 옵션 값에 맞는 옵션 타이틀을 DB에서 찾기
                ProductOption productOption = p.getProductOptions().stream()
                        .filter(option -> option.getOptionValue().equals(optionValue)) // DB에서 옵션 값 비교
                        .findFirst()
                        .orElse(null);
                optionTitle = productOption != null ? productOption.getOptionTitle() : null; // optionTitle 설정
            }

            // 이미지 URL 설정 (기본 URL과 함께 조합)
            String fullImg = null;
            if (p.getMainImg() != null) {
                if (p.getMainImg().startsWith("/")) {
                    fullImg = baseUrl + p.getMainImg(); // 기본 URL을 앞에 붙임
                } else {
                    fullImg = baseUrl + "/" + p.getMainImg(); // 기본 URL을 앞에 붙임
                }
            }

            // 가격 계산 (옵션이 없으면 기본 가격을 사용)
            int price = p.getSellPrice().intValue();

            // 품절 처리 (재고가 0 이하일 경우)
            boolean soldOut = p.getStock() <= 0;

            // CartItemDto 빌드하여 반환
            return CartItemDto.builder()
                    .cartId(cart.getCartId())
                    .productId(p.getProductId())
                    .productName(p.getProductName())
                    .thumbnail(fullImg) // 이미지 URL
                    .quantity(cart.getQuantity())
                    .price(price)  // 가격 (옵션이 없으면 기본 가격)
                    .stock(p.getStock())  // 상품의 재고
                    .soldOut(soldOut)  // 품절 여부
                    .optionValue(optionValue)  // 옵션 값
                    .optionTitle(optionTitle)  // 옵션 타이틀
                    .build();

        }).toList();

        // 총 금액과 총 수량 계산
        int totalPrice = items.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();
        int totalQty = items.stream().mapToInt(CartItemDto::getQuantity).sum();

        // CartResponseDto 반환
        return CartResponseDto.builder()
                .items(items)
                .totalPrice(totalPrice)
                .totalQuantity(totalQty)
                .build();
    }




    @Transactional
    public void changeOption(Long memberId, Long cartId, String newOptionValue) {
        // 1. 장바구니 항목을 memberId와 cartId를 기준으로 찾습니다.
        Cart cart = cartRepository.findByCartIdAndMember_Id(cartId, memberId)
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다."));

        // 2. 기존 상품과 옵션 값을 가져옵니다.
        Product product = cart.getProduct();
        String currentOptionValue = cart.getOptionValue();  // 현재 저장된 옵션 값

        // 3. 옵션 값이 바뀌었으면, 새로운 옵션 값으로 업데이트합니다.
        if (!currentOptionValue.equals(newOptionValue)) {
            cart.setOptionValue(newOptionValue);  // 새로운 옵션 값으로 변경
        }

        // 4. 필요시, 가격 또는 재고 등을 체크할 수 있습니다.
        // 예를 들어, 새로운 옵션에 대해 재고를 체크하거나, 가격을 업데이트하는 추가 로직을 구현할 수 있습니다.
        // 추가적인 로직을 원하시면 이곳에 처리하면 됩니다.

        // 5. 장바구니 항목 업데이트 후, 변경된 데이터를 저장합니다.
        cartRepository.save(cart);  // 장바구니 항목 저장 (옵션 값 업데이트)
    }


    /** -------------------------
     * 수량 변경 (동시성 보호)
     * ------------------------- */
    @Transactional
    public void updateQuantity(Long memberId, Long cartId, int quantity) {

        if (quantity <= 0)
            throw new IllegalArgumentException("수량은 1 이상");

        /** row-level lock */
        Cart cart = cartRepository.findForUpdate(cartId, memberId)
                .orElseThrow(() -> new RuntimeException("장바구니 없음"));

        Product product = cart.getProduct();

        int stock = product.getStock();  // 옵션이 없으면 상품의 재고 사용

        if (quantity > stock)
            throw new IllegalArgumentException("재고 부족");

        cart.setQuantity(quantity);  // 수량 업데이트
    }

    /** -------------------------
     * 삭제
     * ------------------------- */
    @Transactional
    public void delete(Long cartId, Long memberId) {

        Cart cart = cartRepository.findByCartIdAndMember_Id(cartId, memberId)
                .orElseThrow(() -> new RuntimeException("장바구니 없음"));

        cartRepository.delete(cart);  // 장바구니 항목 삭제
    }
}
