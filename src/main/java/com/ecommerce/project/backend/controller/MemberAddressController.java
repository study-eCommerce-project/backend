package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.MemberAddressDto;
import com.ecommerce.project.backend.service.MemberAddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class MemberAddressController {

    private final MemberAddressService addressService;

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestBody MemberAddressDto dto,
            HttpServletRequest request) {

        Member loginMember = (Member) request.getSession().getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(401).body("로그인 필요");

        addressService.addAddress(loginMember.getId(), dto);
        return ResponseEntity.ok("배송지 추가 성공");
    }

    @GetMapping
    public ResponseEntity<?> getMyAddresses(HttpServletRequest request) {
        Member loginMember = (Member) request.getSession().getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(401).body("로그인 필요");

        return ResponseEntity.ok(addressService.getMyAddresses(loginMember.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpServletRequest request) {

        Member loginMember = (Member) request.getSession().getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(401).body("로그인 필요");

        addressService.deleteAddress(id, loginMember.getId());
        return ResponseEntity.ok("삭제 완료");
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<?> setDefaultAddress(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Member loginMember = (Member) request.getSession().getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(401).body("로그인 필요");

        addressService.setDefaultAddress(loginMember.getId(), id);
        return ResponseEntity.ok("기본 배송지 설정 완료");
    }

}
