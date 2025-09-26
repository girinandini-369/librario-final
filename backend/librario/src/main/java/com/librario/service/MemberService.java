package com.librario.service;

import com.librario.dto.MemberDto;
import com.librario.dto.MemberRequest;
import com.librario.entity.Member;
import com.librario.entity.MembershipPlan;
import com.librario.entity.Role;
import com.librario.entity.User;
import com.librario.repository.MemberRepository;
import com.librario.repository.MembershipPlanRepository;
import com.librario.repository.UserRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final RoleRepository roleRepository;

    private MemberDto toDto(Member member) {
        if (member == null) return null;

        return MemberDto.builder()
                .id(member.getId())
                .userId(member.getUser() != null ? member.getUser().getId() : null)
                .userName(member.getName())
                .userEmail(member.getEmail())
                .status(member.getStatus())
                .membershipPlanName(member.getMembershipPlan() != null
                        ? member.getMembershipPlan().getName()
                        : "NO PLAN")
                .startDate(member.getStartDate())
                .endDate(member.getEndDate())
                .createdAt(member.getCreatedAt())
                .build();
    }

    /** ➕ Register a member (admin or offline user) */
    public MemberDto registerMember(MemberRequest request) {
        User user = null;

        // If userId is provided, attach to existing user
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("❌ User not found with id " + request.getUserId()));
        } else {
            // Try finding user by email
            if (request.getEmail() != null) {
                user = userRepository.findByEmail(request.getEmail()).orElse(null);
            }
            // If still null, create new offline user
            if (user == null) {
                user = new User();
                user.setName(request.getName());
                user.setEmail(request.getEmail());
                user.setPassword("default123"); // default password

                // Assign default role
                Role defaultRole = roleRepository.findByRoleNameIgnoreCase("USER")
                        .orElseThrow(() -> new RuntimeException("❌ Default role USER not found. Please insert into roles table."));
                user.setRole(defaultRole);

                user = userRepository.save(user);
            }
        }

        MembershipPlan plan = resolvePlan(request);

        String name = request.getName() != null ? request.getName() : user.getName();
        String email = request.getEmail() != null ? request.getEmail() : user.getEmail();

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        LocalDate endDate = request.getEndDate();

        if (plan != null && endDate == null && plan.getDurationDays() != null) {
            endDate = startDate.plusDays(plan.getDurationDays());
        }

        Member member = Member.builder()
                .user(user)
                .membershipPlan(plan)
                .name(name)
                .email(email)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(LocalDateTime.now())
                .build();

        return toDto(memberRepository.save(member));
    }

    /** ✏️ Update member */
    public MemberDto updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Member not found with id " + id));

        if (request.getName() != null) member.setName(request.getName());
        if (request.getEmail() != null) member.setEmail(request.getEmail());
        if (request.getStatus() != null) member.setStatus(request.getStatus());

        MembershipPlan plan = resolvePlan(request);
        if (plan != null) {
            applyPlan(member, plan);
        }

        return toDto(memberRepository.save(member));
    }

    /** Helper: resolve plan by ID first, fallback to name */
    private MembershipPlan resolvePlan(MemberRequest request) {
        if (request.getPlanId() != null) {
            return membershipPlanRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("❌ Membership Plan not found with id " + request.getPlanId()));
        } else if (request.getMembershipPlanName() != null) {
            return membershipPlanRepository.findByName(request.getMembershipPlanName())
                    .orElseThrow(() -> new RuntimeException("❌ Membership Plan not found with name " + request.getMembershipPlanName()));
        }
        return null;
    }

    /** Helper: apply plan and update endDate */
    private void applyPlan(Member member, MembershipPlan plan) {
        member.setMembershipPlan(plan);
        if (plan.getDurationDays() != null) {
            LocalDate newEndDate = member.getStartDate() != null
                    ? member.getStartDate().plusDays(plan.getDurationDays())
                    : LocalDate.now().plusDays(plan.getDurationDays());
            member.setEndDate(newEndDate);
        }
    }

    /** 📋 Get all members */
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream().map(this::toDto).toList();
    }

    /** 🔍 Get member by ID */
    public MemberDto getMemberById(Long id) {
        return memberRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("❌ Member not found with id " + id));
    }

    /** 🔍 Get member by Email */
    public MemberDto getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("❌ Member not found with email " + email));
    }

    /** 🔍 Get member by UserId */
    public MemberDto getMemberByUserId(Long userId) {
        return memberRepository.findByUserId(userId).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("❌ Member not found with userId " + userId));
    }

    /** ❌ Delete member */
    public boolean deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("❌ Member not found with id " + id);
        }
        if (borrowRecordRepository.existsByMemberId(id)) {
            throw new RuntimeException("❌ Cannot delete member with active borrow records");
        }
        memberRepository.deleteById(id);
        return true;
    }
}
