package com.librario.service;

import com.librario.dto.BorrowRecordDTO;
import com.librario.entity.BorrowRecord;
import com.librario.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OverdueService {

    private final BorrowRecordRepository borrowRepo;
    private final BorrowRecordService borrowRecordService;

    public List<BorrowRecordDTO> checkOverdues() {
        LocalDate today = LocalDate.now();

        List<BorrowRecord> overdueRecords = borrowRepo.findAll().stream()
                .filter(r -> "BORROWED".equalsIgnoreCase(r.getStatus()))
                .filter(r -> r.getDueDate() != null && today.isAfter(r.getDueDate()))
                .peek(r -> {
                    r.setStatus("OVERDUE");
                    borrowRepo.save(r);
                })
                .collect(Collectors.toList());

        // Convert to DTOs
        return overdueRecords.stream()
                .map(borrowRecordService::mapToDTO) // ✅ now valid because it's public
                .collect(Collectors.toList());
    }
}
