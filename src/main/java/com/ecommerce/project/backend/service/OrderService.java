package com.ecommerce.project.backend.service;

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

    @Transactional
    public OrderDto checkout(Long memberId, Long addressId) {

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
                        .zipcode("00000") // 필요하면 바꿔라
                        .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                        .totalPrice(totalPrice)
                        .paymentMethod("POINT")
                        .status("PAID")
                        .build()
        );


        // -------------------------------
        // 6) 주문 상세 저장 + 재고 차감 + DTO 변환
        // -------------------------------
        for (Cart c : carts) {

            Product p = c.getProduct();
            ProductOption opt = c.getOption();
            int qty = c.getQuantity();

            BigDecimal unitPrice = (opt != null) ? opt.getSellPrice() : p.getSellPrice();

            // DB에 실제 저장되는 OrderItem
            OrderItem orderItem = orderItemRepository.save(
                    OrderItem.builder()
                            .order(order)
                            .product(p)
                            .option(opt)
                            .quantity(qty)
                            .price(unitPrice)
                            .mainImg(p.getMainImg()) // 🔥 DB 저장
                            .productName(p.getProductName()) // 🔥 DB 저장
                            .optionValue(opt != null ? opt.getOptionValue() : null) // 🔥 DB 저장
                            .build()
            );

            // 프론트로 보낼 DTO
            itemDtos.add(
                    OrderItemDto.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .orderId(order.getOrderId())
                            .productId(p.getProductId())
                            .productName(orderItem.getProductName())
                            .mainImg(orderItem.getMainImg())
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
            } else {
                p.setStock(p.getStock() - qty);
            }

            productRepository.save(p);
        }


        // -------------------------------
        // 7) 장바구니 비우기
        // -------------------------------
        cartRepository.deleteAll(carts);


        // -------------------------------
        // 8) 프론트로 반환할 DTO 생성
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
}
