package com.librario.service;

import com.librario.dto.BorrowRecordDTO;
import com.librario.entity.Book;
import com.librario.entity.BorrowRecord;
import com.librario.entity.Member;
import com.librario.entity.User;
import com.librario.repository.BookRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.MemberRepository;
import com.librario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DashboardUserService — returns DTOs to the UI and does not expose JPA entities directly.
 * Make sure BorrowRecordRepository uses BorrowRecord (entity) as the generic type.
 */
@Service
@RequiredArgsConstructor
public class DashboardUserService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    /**
     * Get currently borrowed (active) books for a member.
     */
    public List<BorrowRecordDTO> getMyBooks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        List<BorrowRecord> records = borrowRecordRepository.findByMemberAndStatus(member, "BORROWED");
        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Get books that were returned by the member.
     */
    public List<BorrowRecordDTO> getReturnBooks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        List<BorrowRecord> records = borrowRecordRepository.findByMemberAndStatus(member, "RETURNED");
        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Get overdue books for a member (not yet returned and past due date).
     */
    public List<BorrowRecordDTO> getOverdueBooks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        LocalDate today = LocalDate.now();
        List<BorrowRecord> records = borrowRecordRepository.findByMember(member);
        return records.stream()
                .filter(r -> r.getDueDate() != null && r.getReturnDate() == null && today.isAfter(r.getDueDate()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending (request) records for a member.
     */
    public List<BorrowRecordDTO> getPendingBooks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        List<BorrowRecord> records = borrowRecordRepository.findByMemberAndStatus(member, "PENDING");
        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Return the User entity (membership details). Caller can map properties as needed.
     */
    public User getMembershipDetails(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    /**
     * Search for available books (title/author/genre). Only returns books with availableCopies > 0.
     */
    public List<Book> searchAvailableBooks(String query) {
        // Ensure your BookRepository declares this method:
        // List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(String t, String a, String g);
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                        query, query, query
                ).stream()
                .filter(book -> book.getAvailableCopies() != null && book.getAvailableCopies() > 0)
                .collect(Collectors.toList());
    }

    // ------------------- Helper: entity -> DTO mapper -------------------

    private BorrowRecordDTO toDTO(BorrowRecord record) {
        if (record == null) return null;

        // member name resolution (safe)
        String memberName = "Unknown";
        if (record.getMember() != null) {
            if (record.getMember().getName() != null && !record.getMember().getName().isBlank()) {
                memberName = record.getMember().getName();
            } else if (record.getMember().getUser() != null && record.getMember().getUser().getName() != null) {
                memberName = record.getMember().getUser().getName();
            }
        }

        // book title resolution (safe)
        String bookTitle = record.getBook() != null && record.getBook().getTitle() != null
                ? record.getBook().getTitle() : "Unknown";

        // fine calculation:
        Double fine = record.getFine(); // if DB holds fine already, use it
        // if none in DB, compute if overdue and not returned
        LocalDate today = LocalDate.now();
        if ((fine == null || fine == 0.0) && record.getDueDate() != null) {
            // If returned, and returnDate after due => compute based on returnDate
            if (record.getReturnDate() != null && record.getReturnDate().isAfter(record.getDueDate())) {
                long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), record.getReturnDate());
                fine = daysLate > 0 ? daysLate * 5.0 : 0.0; // default ₹5/day
            }
            // If not returned and past due -> compute until today
            else if (record.getReturnDate() == null && today.isAfter(record.getDueDate())) {
                long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), today);
                fine = daysLate > 0 ? daysLate * 5.0 : 0.0; // default ₹5/day
            } else {
                fine = 0.0;
            }
        }

        return BorrowRecordDTO.builder()
                .id(record.getId())
                .memberId(record.getMember() != null ? record.getMember().getId() : null)
                .memberName(memberName)
                .bookId(record.getBook() != null ? record.getBook().getId() : null)
                .bookTitle(bookTitle)
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .fine(fine)
                .build();
    }
}
