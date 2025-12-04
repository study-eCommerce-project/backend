package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.repository.OrderRepository;
import com.ecommerce.project.backend.service.OrderService;
import com.ecommerce.project.backend.service.PortOneService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PortOneService portOneService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {

        try {
            JsonNode info = portOneService.verifyPayment(req.getPaymentId());

            String status = info.get("status").asText();
            int paidAmount = info.get("amount").get("total").asInt();

            var order = orderRepository.findById(req.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문 없음"));

            int orderAmount = order.getTotalPrice().intValue();

            if (orderAmount != paidAmount)
                return ResponseEntity.badRequest().body("결제 금액 불일치");

            if (!status.equals("PAID"))
                return ResponseEntity.badRequest().body("결제가 완료되지 않음");

            orderService.completeCardPayment(req.getOrderId());

            return ResponseEntity.ok("결제 성공");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Data
    public static class VerifyRequest {
        private String paymentId;
        private Long orderId;
    }
}
