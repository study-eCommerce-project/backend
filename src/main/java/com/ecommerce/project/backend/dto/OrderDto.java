//package com.ecommerce.project.backend.dto;
//
//import com.ecommerce.project.backend.domain.Order;
//import com.ecommerce.project.backend.domain.OrderItem;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class OrderDto {
//
//    private String orderNumber;
//    private BigDecimal totalPrice;
//    private String status;
//    private String paymentMethod;
//
//    // 🔥 배송지 스냅샷
//    private String receiverName;
//    private String receiverPhone;
//    private String address;
//    private String addressDetail;
//    private String zipcode;
//
//    private List<OrderItemDto> items;
//
//    public static OrderDto fromEntity(Order order, List<OrderItem> orderItems) {
//        return OrderDto.builder()
//                .orderNumber(order.getOrderNumber())
//                .totalPrice(order.getTotalPrice())
//                .status(order.getStatus())
//                .paymentMethod(order.getPaymentMethod())
//
//                // 🔥 배송지 스냅샷 매핑
//                .receiverName(order.getReceiverName())
//                .receiverPhone(order.getReceiverPhone())
//                .address(order.getAddress())
//                .addressDetail(order.getAddressDetail())
//                .zipcode(order.getZipcode())
//
//                // 🔥 주문 상세 목록 변환
//                .items(orderItems.stream()
//                        .map(OrderItemDto::fromEntity)
//                        .toList()
//                )
//                .build();
//    }
//}
package com.ecommerce.project.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String orderNumber;
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;

    private MemberAddressDto address;        // 프론트에서 바로 사용 가능!
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
}
