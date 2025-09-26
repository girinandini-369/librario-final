package com.librario.service;

import com.librario.dto.BorrowRecordDTO;
import com.librario.entity.Book;
import com.librario.entity.BorrowRecord;
import com.librario.entity.BorrowRequest;
import com.librario.entity.Member;
import com.librario.repository.BookRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.BorrowRequestRepository;
import com.librario.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRequestService {

    private final BorrowRequestRepository requestRepo;
    private final MemberRepository memberRepo;
    private final BookRepository bookRepo;
    private final BorrowRecordRepository recordRepo;

    // 📌 Create new borrow request
    public BorrowRequest createRequest(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("❌ Member not found"));
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("❌ Book not found"));

        BorrowRequest request = BorrowRequest.builder()
                .member(member)
                .book(book)
                .status("PENDING")
                .requestDate(LocalDateTime.now())
                .build();

        return requestRepo.save(request);
    }

    // 📌 Approve request → create BorrowRecord and return DTO
    public BorrowRecordDTO approveRequest(Long requestId) {
        BorrowRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("❌ Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("❌ Request already processed");
        }

        request.setStatus("APPROVED");
        request.setResponseDate(LocalDateTime.now());
        requestRepo.save(request);

        BorrowRecord record = BorrowRecord.builder()
                .member(request.getMember())
                .book(request.getBook())
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status("BORROWED")
                .fine(0.0) // ensure fine field is set
                .build();

        recordRepo.save(record);

        return mapToDTO(record);
    }

    // 📌 Reject request
    public BorrowRequest rejectRequest(Long requestId) {
        BorrowRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("❌ Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("❌ Request already processed");
        }

        request.setStatus("REJECTED");
        request.setResponseDate(LocalDateTime.now());

        return requestRepo.save(request);
    }

    // 📌 Get all requests
    public List<BorrowRequest> getAllRequests() {
        return requestRepo.findAll();
    }

    // 📌 Get only pending requests
    public List<BorrowRequest> getPendingRequests() {
        return requestRepo.findByStatus("PENDING");
    }

    // 📌 Helper: map BorrowRecord → DTO
    private BorrowRecordDTO mapToDTO(BorrowRecord record) {
        return BorrowRecordDTO.builder()
                .id(record.getId())
                .memberId(record.getMember() != null ? record.getMember().getId() : null)
                .memberName(record.getMember() != null ? record.getMember().getName() : "Unknown")
                .bookId(record.getBook() != null ? record.getBook().getId() : null)
                .bookTitle(record.getBook() != null ? record.getBook().getTitle() : "Unknown")
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .fine(record.getFine() != null ? record.getFine() : 0.0)
                .build();
    }
}
