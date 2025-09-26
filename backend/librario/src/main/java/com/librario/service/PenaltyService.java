package com.librario.service;

import com.librario.dto.PenaltyRequest;
import com.librario.entity.BorrowRecord;
import com.librario.entity.Penalty;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * Create a penalty linked to an existing BorrowRecord (entity).
     */
    public Penalty createPenalty(PenaltyRequest request) {
        BorrowRecord record = borrowRecordRepository.findById(request.getBorrowRecordId())
                .orElseThrow(() -> new RuntimeException("Borrow record not found: " + request.getBorrowRecordId()));

        Penalty penalty = Penalty.builder()
                .borrowRecord(record)
                .memberId(record.getMember().getId())
                .amount(request.getAmount())
                .reason(request.getReason())
                .createdAt(LocalDate.now())
                .build();

        return penaltyRepository.save(penalty);
    }

    public List<Penalty> getAllPenalties() {
        return penaltyRepository.findAll();
    }

    public List<Penalty> getPenaltiesByMember(Long memberId) {
        return penaltyRepository.findByMemberId(memberId);
    }
}
