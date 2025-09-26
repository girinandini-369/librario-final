package com.librario.repository;

import com.librario.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMemberId(Long memberId);
    List<Transaction> findByBookId(Long bookId);
}
