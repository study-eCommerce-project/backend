package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.OrderDto;
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
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.status(401).build();

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(401).build();

        Long addressId = Long.valueOf(data.get("addressId").toString());

        OrderDto order = orderService.checkout(loginMember.getId(), addressId);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getOrderHistory(HttpSession session) {

        Member member = (Member) session.getAttribute("loginMember");
        if (member == null) {
            return ResponseEntity.status(401).body("NOT_LOGIN");
        }

        Long memberId = member.getId(); // Member → memberId 변환

        return ResponseEntity.ok(orderService.getOrderHistory(memberId));
    }

}
