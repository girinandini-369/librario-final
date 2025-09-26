package com.librario.service;

import com.librario.dto.BorrowRecordDTO;
import com.librario.entity.Book;
import com.librario.entity.BorrowRecord;
import com.librario.entity.Member;
import com.librario.repository.BookRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {

    private final BorrowRecordRepository recordRepo;
    private final MemberRepository memberRepo;
    private final BookRepository bookRepo;
    private final EmailService emailService;

    // ---------------- CREATE ----------------

    // Request book (PENDING)
    public BorrowRecordDTO requestBook(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        BorrowRecord record = BorrowRecord.builder()
                .member(member)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status("PENDING")
                .fine(0.0)
                .build();

        BorrowRecord saved = recordRepo.save(record);

        // send email
        emailService.sendEmail(
                member.getEmail(),
                "Book Request Submitted",
                "You have requested the book: " + book.getTitle() + ". Status: PENDING."
        );

        return mapToDTO(saved);
    }

    // Immediate borrow
    public BorrowRecordDTO createBorrow(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        BorrowRecord record = BorrowRecord.builder()
                .member(member)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status("BORROWED")
                .fine(0.0)
                .build();

        BorrowRecord saved = recordRepo.save(record);

        // send email
        emailService.sendEmail(
                member.getEmail(),
                "Book Borrowed",
                "You have borrowed the book: " + book.getTitle() + ". Due on: " + saved.getDueDate()
        );

        return mapToDTO(saved);
    }

    // ---------------- ACTIONS ----------------

    // Return book
    public BorrowRecordDTO returnBook(Long id) {
        BorrowRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
        record.setReturnDate(LocalDate.now());
        record.setStatus("RETURNED");
        BorrowRecord saved = recordRepo.save(record);

        // send email
        emailService.sendEmail(
                record.getMember().getEmail(),
                "Book Returned",
                "You have returned the book: " + record.getBook().getTitle()
        );

        return mapToDTO(saved);
    }

    // Approve request -> mark as BORROWED
    public BorrowRecordDTO approveRequest(Long id) {
        BorrowRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
        record.setStatus("BORROWED");
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        BorrowRecord saved = recordRepo.save(record);

        // send email
        emailService.sendEmail(
                record.getMember().getEmail(),
                "Book Request Approved",
                "Your request for book: " + record.getBook().getTitle() + " has been approved. Due date: " + saved.getDueDate()
        );

        return mapToDTO(saved);
    }

    // Reject request
    public BorrowRecordDTO rejectRequest(Long id) {
        BorrowRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
        record.setStatus("REJECTED");
        BorrowRecord saved = recordRepo.save(record);

        // send email
        emailService.sendEmail(
                record.getMember().getEmail(),
                "Book Request Rejected",
                "Your request for book: " + record.getBook().getTitle() + " has been rejected."
        );

        return mapToDTO(saved);
    }

    // Pay fine
    public BorrowRecordDTO payFine(Long id) {
        BorrowRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
        record.setFine(0.0);
        BorrowRecord saved = recordRepo.save(record);

        // send email
        emailService.sendEmail(
                record.getMember().getEmail(),
                "Fine Paid",
                "Your fine for book: " + record.getBook().getTitle() + " has been cleared."
        );

        return mapToDTO(saved);
    }

    // ---------------- QUERIES ----------------

    public List<BorrowRecordDTO> getAllRecords() {
        return recordRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getRecordsByMember(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return recordRepo.findByMember(member).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getBorrowedByMember(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return recordRepo.findByMemberAndStatus(member, "BORROWED").stream()
                .filter(r -> r.getReturnDate() == null)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getOverdueBooksByMember(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        LocalDate today = LocalDate.now();
        return recordRepo.findByMemberAndStatus(member, "BORROWED").stream()
                .filter(r -> r.getDueDate() != null && r.getReturnDate() == null && r.getDueDate().isBefore(today))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getPendingByMember(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return recordRepo.findByMemberAndStatus(member, "PENDING").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getReturnedByMember(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return recordRepo.findByMemberAndStatus(member, "RETURNED").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getAllPendingRequests() {
        return recordRepo.findByStatus("PENDING").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowRecordDTO> getAllOverdueRecords() {
        LocalDate today = LocalDate.now();
        return recordRepo.findByStatus("BORROWED").stream()
                .filter(r -> r.getDueDate() != null && r.getReturnDate() == null && r.getDueDate().isBefore(today))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- HELPER ----------------

    public BorrowRecordDTO mapToDTO(BorrowRecord record) {
        return BorrowRecordDTO.builder()
                .id(record.getId())
                .memberId(record.getMember() != null ? record.getMember().getId() : null)
                .memberName(record.getMember() != null ? record.getMember().getName() : null)
                .bookId(record.getBook() != null ? record.getBook().getId() : null)
                .bookTitle(record.getBook() != null ? record.getBook().getTitle() : null)
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .fine(record.getFine())
                .build();
    }
}
