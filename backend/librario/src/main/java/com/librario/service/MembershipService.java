package com.librario.service;

import com.librario.dto.MembershipPlanRequest;
import com.librario.entity.MembershipPlan;
import com.librario.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipPlanRepository membershipRepository;

    public MembershipPlan createPlan(MembershipPlanRequest request) {
        MembershipPlan plan = MembershipPlan.builder()
                .name(request.getName())
                .price(request.getPrice())
                .maxBooks(request.getMaxBooks())
                .durationDays(request.getDurationDays())
                .borrowingLimit(request.getBorrowingLimit())
                .duration(request.getDuration())
                .fees(request.getFees())
                .type(request.getType())
                .description(request.getDescription())
                .finePerDay(request.getFinePerDay() != null ? request.getFinePerDay() : 10.0) // ✅ default
                .build();
        return membershipRepository.save(plan);
    }

    public List<MembershipPlan> getAllPlans() {
        return membershipRepository.findAll();
    }

    public MembershipPlan updatePlan(Long id, MembershipPlanRequest updated) {
        return membershipRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setPrice(updated.getPrice());
                    existing.setMaxBooks(updated.getMaxBooks());
                    existing.setDurationDays(updated.getDurationDays());
                    existing.setBorrowingLimit(updated.getBorrowingLimit());
                    existing.setDuration(updated.getDuration());
                    existing.setFees(updated.getFees());
                    existing.setType(updated.getType());
                    existing.setDescription(updated.getDescription());
                    if (updated.getFinePerDay() != null) {
                        existing.setFinePerDay(updated.getFinePerDay()); // ✅ update fine
                    }
                    return membershipRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Plan not found with id " + id));
    }

    public boolean deletePlan(Long id) {
        if (membershipRepository.existsById(id)) {
            membershipRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
