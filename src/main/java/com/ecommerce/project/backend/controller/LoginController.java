package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.LoginRequest;
import com.ecommerce.project.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {

    private final MemberRepository memberRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        String email = request.getEmail();
        String pw = request.getPassword();

        Optional<Member> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(404).body("존재하지 않는 사용자입니다.");
        }

        Member member = memberOpt.get();

        // 비밀번호 비교
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(pw, member.getPassword())) {
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }

        // 🔥 로그인 성공 → 세션 저장
        session.setAttribute("user", member);

        // 프론트에서 사용할 유저 정보 반환(JSON)
        return ResponseEntity.ok(
                Map.of(
                        "id", member.getId(),
                        "email", member.getEmail(),
                        "name", member.getName(),
                        "role", member.getRole()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }
}
