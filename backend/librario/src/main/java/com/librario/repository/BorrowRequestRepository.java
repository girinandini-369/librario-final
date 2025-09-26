package com.librario.repository;

import com.librario.entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {
    List<BorrowRequest> findByMemberId(Long memberId);
    List<BorrowRequest> findByStatus(String status);
}
