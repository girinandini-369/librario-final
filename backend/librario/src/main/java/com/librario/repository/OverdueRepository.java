package com.librario.repository;

import com.librario.entity.Overdue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OverdueRepository extends JpaRepository<Overdue, Long> {
    List<Overdue> findByMemberId(Long memberId);
}
