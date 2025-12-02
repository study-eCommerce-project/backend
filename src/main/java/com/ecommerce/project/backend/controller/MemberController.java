package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.MemberDto;
import com.ecommerce.project.backend.repository.MemberRepository;
import com.ecommerce.project.backend.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberDto request) {
        try {
            memberService.signup(request); // DTO 기반 서비스 호출
            return ResponseEntity.ok("회원가입 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body("회원가입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        try {
            String tempPw = memberService.resetPassword(email);
            return ResponseEntity.ok("임시 비밀번호가 발급되었습니다: " + tempPw);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** 내 정보 수정 */
    @PutMapping("/update")
    public ResponseEntity<?> updateMyInfo(
            @RequestBody MemberDto dto,
            HttpServletRequest request
    ) {
        Member loginMember = (Member) request.getSession().getAttribute("loginMember");

        if (loginMember == null)
            return ResponseEntity.status(401).body("로그인 필요");

        MemberDto updated = memberService.updateMemberInfo(loginMember.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/member/me")
    public ResponseEntity<?> getMyInfo(HttpServletRequest request) {

        Member loginMember = (Member) request.getSession().getAttribute("loginMember");

        if (loginMember == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        // DTO로 변환해서 리턴
        MemberDto dto = MemberDto.fromEntity(loginMember);

        return ResponseEntity.ok(dto);
    }

}


