package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.OrderDto;
import com.ecommerce.project.backend.dto.OrderRequestDTO;
import com.ecommerce.project.backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequestDTO req,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null)
            return ResponseEntity.status(401).body("NOT_LOGIN");

        try {
            // 실제 주문 생성
            OrderDto order = orderService.checkout(memberId, req);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e) {
            // 비즈니스 예외 (포인트 부족, 재고 부족 등)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (RuntimeException e) {
            // 서비스에서 throw new RuntimeException() 한 것들
            return ResponseEntity.status(400).body(e.getMessage());
        }
        catch (Exception e) {
            // 진짜 예기치 못한 서버 에러
            return ResponseEntity.status(500).body("서버 오류");
        }
    }


    @GetMapping("/history")
    public ResponseEntity<?> getOrderHistory(HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            return ResponseEntity.status(401).body("NOT_LOGIN");
        }

        return ResponseEntity.ok(orderService.getOrderHistory(memberId));
    }

    /** 카드 결제 주문 생성, 결제창 띄우기 전에 Order 생성 (status=READY) */
    @PostMapping("/checkout/card")
    public ResponseEntity<?> checkoutForCard(
            @RequestBody OrderRequestDTO req,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            return ResponseEntity.status(401).body("NOT_LOGIN");
        }

        return ResponseEntity.ok(orderService.checkoutForCard(memberId, req));
    }
}
