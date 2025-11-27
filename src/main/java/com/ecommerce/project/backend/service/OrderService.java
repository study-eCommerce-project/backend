//package com.ecommerce.project.backend.service;
//
//import com.ecommerce.project.backend.domain.*;
//
//import com.ecommerce.project.backend.dto.*;
//import com.ecommerce.project.backend.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.domain.*;
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

    /**
     * 주문 생성: (1) 일반 방식 or (2) 포인트 차감 방식 중 선택 가능
     */
    @Transactional
    public OrderDto checkout(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        List<Cart> carts = cartRepository.findByMember_Id(memberId);
        if (carts.isEmpty()) throw new RuntimeException("장바구니가 비어 있습니다.");

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItemDto> itemDtos = new ArrayList<>();


        /* -----------------------------------------
         * 1) 재고 체크 + 단가 계산
         * ----------------------------------------- */
        for (Cart c : carts) {

            Product p = c.getProduct();
            ProductOption opt = c.getOption();
            int qty = c.getQuantity();

            BigDecimal unitPrice;

            // 옵션상품 → option.sell_price
            if (opt != null) {
                unitPrice = opt.getSellPrice();

                if (opt.getStock() < qty)
                    throw new RuntimeException("옵션 재고 부족: " + opt.getOptionValue());

            } else {
                // 단일상품 → product.sell_price
                unitPrice = p.getSellPrice();

                if (p.getStock() < qty)
                    throw new RuntimeException("상품 재고 부족: " + p.getProductName());
            }

            totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
        }


        /* -----------------------------------------
         * 2) 포인트 결제
         * ----------------------------------------- */
        BigDecimal memberPoint = BigDecimal.valueOf(member.getPoint());

        if (memberPoint.compareTo(totalPrice) < 0) {
            throw new RuntimeException("포인트 부족 (필요: " + totalPrice + ")");
        }

        member.setPoint(memberPoint.subtract(totalPrice).intValue());
        memberRepository.save(member);


        /* -----------------------------------------
         * 3) 주문 생성
         * ----------------------------------------- */
        Order order = orderRepository.save(
                Order.builder()
                        .member(member)
                        .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                        .totalPrice(totalPrice)
                        .paymentMethod("POINT")
                        .status("PAID")
                        .build()
        );


        /* -----------------------------------------
         * 4) 주문 상세 생성 + 재고차감
         * ----------------------------------------- */
        for (Cart c : carts) {

            Product p = c.getProduct();
            ProductOption opt = c.getOption();
            int qty = c.getQuantity();

            BigDecimal unitPrice =
                    (opt == null) ? p.getSellPrice() : opt.getSellPrice();

            // ORDER ITEM 생성
            OrderItem orderItem = orderItemRepository.save(
                    OrderItem.builder()
                            .order(order)
                            .product(p)
                            .option(opt)
                            .quantity(qty)
                            .price(unitPrice)
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
            itemDtos.add(OrderItemDto.fromEntity(orderItem));
        }


        /* -----------------------------------------
         * 5) 옵션상품이면 product.stock 재계산
         * ----------------------------------------- */
        for (Cart c : carts) {
            Product p = c.getProduct();

            List<ProductOption> options = productOptionRepository.findByProduct_ProductId(p.getProductId());
            if (!options.isEmpty()) {
                int newStock = options.stream().mapToInt(ProductOption::getStock).sum();
                p.setStock(newStock);
                productRepository.save(p);
            }
        }


        /* -----------------------------------------
         * 6) 장바구니 비우기
         * ----------------------------------------- */
        cartRepository.deleteAll(carts);


        /* -----------------------------------------
         * 7) 결과 반환
         * ----------------------------------------- */
        return OrderDto.builder()
                .orderNumber(order.getOrderNumber())
                .totalPrice(order.getTotalPrice())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .items(itemDtos)
                .build();
    }

}







