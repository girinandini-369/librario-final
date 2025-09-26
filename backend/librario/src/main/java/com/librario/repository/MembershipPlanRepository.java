package com.librario.repository;

import com.librario.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    Optional<MembershipPlan> findByName(String name);
}
