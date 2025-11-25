//
//
//package com.ecommerce.project.backend.controller;
//
//import com.ecommerce.project.backend.domain.Member;
//import com.ecommerce.project.backend.dto.LoginRequestDto;
//import com.ecommerce.project.backend.repository.MemberRepository;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
//
//public class LoginController {
//
//    private final MemberRepository memberRepository;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpSession session) {
//
//        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
//        if (memberOpt.isEmpty()) {
//            return ResponseEntity.status(404).body("존재하지 않는 사용자입니다.");
//        }
//
//        Member member = memberOpt.get();
//
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        if (!encoder.matches(request.getPassword(), member.getPassword())) {
//            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
//        }
//
//        // ⭐ 세션에 memberId 저장
//        session.setAttribute("user", member);
//
//        return ResponseEntity.ok(
//                Map.of(
//                        "id", member.getId(),
//                        "email", member.getEmail(),
//                        "name", member.getName(),
//                        "role", member.getRole().toUpperCase()
//                )
//        );
//    }
//
//    @GetMapping("/auth/me")
//    public ResponseEntity<?> me(HttpSession session) {
//        Object user = session.getAttribute("user");
//
//        if (user == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//
//        Member member = (Member) user;
//        session.setAttribute("memberId", member.getId());
//
//        return ResponseEntity.ok(
//                Map.of(
//                        "id", member.getId(),
//                        "email", member.getEmail(),
//                        "name", member.getName(),
//                        "role", member.getRole()
//                )
//        );
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpSession session) {
//        session.invalidate();
//        return ResponseEntity.ok("로그아웃 성공");
//    }
//
//}

package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.dto.LoginRequestDto;
import com.ecommerce.project.backend.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final MemberRepository memberRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpSession session) {

        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(404).body("존재하지 않는 사용자입니다.");
        }

        Member member = memberOpt.get();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }

        // ⭐ 세션에 memberId 저장
        session.setAttribute("memberId", member.getId());

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
