package com.librario.repository;

import com.librario.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    List<Penalty> findByMemberId(Long memberId);
}
