package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    /** 로그인된 사용자 정보 조회 */
    @GetMapping("/me")
    public MemberDto getCurrentUser(@AuthenticationPrincipal Member member) {

        // 로그인 안 한 상태
        if (member == null) {
            return null;
        }

        // 로그인 된 사용자 정보 반환
        return MemberDto.fromEntity(member);
    }
}
