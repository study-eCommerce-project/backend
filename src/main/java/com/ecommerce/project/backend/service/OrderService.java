package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.config.MusinsaConfig;
import com.ecommerce.project.backend.domain.*;
import com.ecommerce.project.backend.dto.MemberAddressDto;
import com.ecommerce.project.backend.dto.OrderDto;
import com.ecommerce.project.backend.dto.OrderItemDto;
import com.ecommerce.project.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final MusinsaConfig musinsaConfig;

    @Transactional
    public OrderDto checkout(Long memberId, Long addressId) {

        String baseUrl = musinsaConfig.getImageBaseUrl();

        // -------------------------------
        // 1) 회원 & 배송지 조회
        // -------------------------------
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        MemberAddress address = memberAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("배송지 없음"));


        // -------------------------------
        // 2) 장바구니 조회
        // -------------------------------
        List<Cart> carts = cartRepository.findByMember_Id(memberId);
        if (carts.isEmpty()) throw new RuntimeException("장바구니가 비어 있습니다.");

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItemDto> itemDtos = new ArrayList<>();


        // -------------------------------
        // 3) 총 결제 금액 계산 + 재고 체크
        // -------------------------------
        for (Cart c : carts) {
            Product p = c.getProduct();
            ProductOption opt = c.getOption();
            int qty = c.getQuantity();

            BigDecimal unitPrice = (opt != null) ? opt.getSellPrice() : p.getSellPrice();

            if (opt != null && opt.getStock() < qty)
                throw new RuntimeException("옵션 재고 부족: " + opt.getOptionValue());

            if (opt == null && p.getStock() < qty)
                throw new RuntimeException("상품 재고 부족: " + p.getProductName());

            totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
        }


        // -------------------------------
        // 4) 회원 포인트 차감
        // -------------------------------
        if (member.getPoint() < totalPrice.intValue())
            throw new RuntimeException("포인트 부족");

        member.setPoint(member.getPoint() - totalPrice.intValue());
        memberRepository.save(member);


        // -------------------------------
        // 5) 주문 생성
        // -------------------------------
        Order order = orderRepository.save(
                Order.builder()
                        .member(member)
                        .receiverName(address.getName())
                        .receiverPhone(address.getPhone())
                        .address(address.getAddress())
                        .addressDetail(address.getDetail())
                        .zipcode(address.getZipcode())
                        .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                        .totalPrice(totalPrice)
                        .paymentMethod("POINT")
                        .status("PAID")
                        .build()
        );


        // -------------------------------
        // 6) 주문 상세 저장 + 재고 차감
        // -------------------------------
        Set<Long> updatedProductIds = new HashSet<>();

        for (Cart c : carts) {

            Product p = c.getProduct();
            ProductOption opt = c.getOption();
            int qty = c.getQuantity();

            BigDecimal unitPrice = (opt != null) ? opt.getSellPrice() : p.getSellPrice();

            // 주문 아이템 생성
            OrderItem orderItem = orderItemRepository.save(
                    OrderItem.builder()
                            .order(order)
                            .product(p)
                            .option(opt)
                            .quantity(qty)
                            .price(unitPrice)
                            .mainImg(p.getMainImg())
                            .productName(p.getProductName())
                            .optionValue(opt != null ? opt.getOptionValue() : null)
                            .build()
            );

            // 프론트 반환 DTO
            itemDtos.add(
                    OrderItemDto.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .orderId(order.getOrderId())
                            .productId(p.getProductId())
                            .productName(orderItem.getProductName())
                            .mainImg(baseUrl + orderItem.getMainImg())
                            .optionId(opt != null ? opt.getOptionId() : null)
                            .optionValue(orderItem.getOptionValue())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .subtotal(orderItem.getSubtotal())
                            .build()
            );

            // 재고 차감
            if (opt != null) {
                opt.setStock(opt.getStock() - qty);
                productOptionRepository.save(opt);

                updatedProductIds.add(p.getProductId()); // 옵션 상품만 대상
            } else {
                // 단일 상품은 product.stock 직접 감소
                p.setStock(p.getStock() - qty);
                productRepository.save(p);
            }
        }

        // -------------------------------
        // 7) 옵션상품 재고 합산 → Product.stock 업데이트
        // -------------------------------
        for (Long productId : updatedProductIds) {

            List<ProductOption> optionList =
                    productOptionRepository.findByProduct_ProductId(productId);

            int totalStock = optionList.stream()
                    .mapToInt(ProductOption::getStock)
                    .sum();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("상품 없음"));

            product.setStock(totalStock);
            productRepository.save(product);
        }


        // -------------------------------
        // 8) 장바구니 비우기
        // -------------------------------
        cartRepository.deleteAll(carts);


        // -------------------------------
        // 9) 최종 DTO 반환
        // -------------------------------
        return OrderDto.builder()
                .orderNumber(order.getOrderNumber())
                .totalPrice(order.getTotalPrice())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .address(MemberAddressDto.fromEntity(address))
                .items(itemDtos)
                .build();
    }



    // ================================
    // 주문 내역 조회
    // ================================
    public List<OrderDto> getOrderHistory(Long memberId) {

        String baseUrl = musinsaConfig.getImageBaseUrl();

        List<Order> orders = orderRepository.findByMember_IdOrderByCreatedAtDesc(memberId);

        List<OrderDto> dtos = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItemDto> itemDtos = new ArrayList<>();

            for (OrderItem item : order.getOrderItems()) {

                String fullImg = (item.getMainImg() != null)
                        ? baseUrl + item.getMainImg()
                        : null;

                itemDtos.add(
                        OrderItemDto.builder()
                                .orderItemId(item.getOrderItemId())
                                .orderId(order.getOrderId())
                                .productId(item.getProduct().getProductId())
                                .productName(item.getProductName())
                                .mainImg(fullImg)
                                .optionValue(item.getOptionValue())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(item.getSubtotal())
                                .build()
                );
            }

            MemberAddressDto addressDto = MemberAddressDto.builder()
                    .name(order.getReceiverName())
                    .phone(order.getReceiverPhone())
                    .address(order.getAddress())
                    .detail(order.getAddressDetail())
                    .zipcode(order.getZipcode())
                    .build();

            dtos.add(
                    OrderDto.builder()
                            .orderNumber(order.getOrderNumber())
                            .totalPrice(order.getTotalPrice())
                            .paymentMethod(order.getPaymentMethod())
                            .status(order.getStatus())
                            .address(addressDto)
                            .items(itemDtos)
                            .createdAt(order.getCreatedAt())
                            .build()
            );
        }

        return dtos;
    }
}
