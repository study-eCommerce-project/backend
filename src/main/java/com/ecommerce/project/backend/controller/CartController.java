package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.*;
import com.ecommerce.project.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public void addToCart(
            @AuthenticationPrincipal Member member,
            @RequestBody CartAddRequestDto req
    ) {
        cartService.addToCart(member.getId(), req);
    }


    @GetMapping
    public CartResponseDto getCart(@AuthenticationPrincipal Member member) {
        return cartService.getCart(member.getId());
    }

    @PutMapping("/quantity")
    public void updateQuantity(@AuthenticationPrincipal Member member,
                               @RequestBody CartUpdateQuantityDto req) {
        cartService.updateQuantity(req.getCartId(), req.getQuantity());
    }

    @PutMapping("/option")
    public void changeOption(@AuthenticationPrincipal Member member,
                             @RequestBody CartChangeOptionDto req) {
        cartService.changeOption(member.getId(), req.getCartId(), req.getNewOptionId());
    }


    @DeleteMapping("/{cartId}")
    public void delete(@AuthenticationPrincipal Member member,
                       @PathVariable Long cartId) {
        cartService.delete(cartId, member.getId());
    }

}

