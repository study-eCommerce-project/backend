package com.ecommerce.project.backend.repository;

import com.ecommerce.project.backend.domain.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    // 내 배송지 목록 조회
    List<MemberAddress> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);

    // 기본 배송지 1개만
    MemberAddress findByMemberIdAndIsDefault(Long memberId, boolean isDefault);
}
