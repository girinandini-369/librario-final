// ===================== PaymentRepository.java =====================
package com.librario.repository;

import com.librario.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    // ✅ fetch join to load transaction + member eagerly
    @Query("SELECT p FROM Payment p JOIN FETCH p.transaction t JOIN FETCH t.member m")
    List<Payment> findAllWithTransactionAndMember();

    @Query("SELECT p FROM Payment p JOIN FETCH p.transaction t JOIN FETCH t.member m WHERE m.id = :memberId")
    List<Payment> findByTransaction_Member_IdFetch(Long memberId);
}
