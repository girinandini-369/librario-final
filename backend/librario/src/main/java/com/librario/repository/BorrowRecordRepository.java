package com.librario.repository;

import com.librario.entity.BorrowRecord;
import com.librario.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByMember(Member member);

    List<BorrowRecord> findByMemberAndStatus(Member member, String status);

    List<BorrowRecord> findByStatus(String status);

    List<BorrowRecord> findByStatusAndReturnDateIsNull(String status);

    List<BorrowRecord> findByMemberId(Long memberId);

    List<BorrowRecord> findByBookId(Long bookId);

    boolean existsByMemberId(Long memberId);
}
