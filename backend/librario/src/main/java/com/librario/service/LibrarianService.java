package com.librario.service;

import com.librario.dto.BorrowRecordDTO;
import com.librario.entity.Book;
import com.librario.entity.BorrowRecord;
import com.librario.entity.User;
import com.librario.repository.BookRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibrarianService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowRecordService borrowRecordService;

    // ---------------- DASHBOARD ----------------

    public long countUsers() {
        return userRepository.count();
    }

    public long countBooks() {
        return bookRepository.count();
    }

    public long countBorrowed() {
        return borrowRecordRepository.findByStatus("BORROWED").size();
    }

    // ---------------- BOOK MANAGEMENT ----------------

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            return bookRepository.save(book);
        }).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // ---------------- USER MANAGEMENT ----------------

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ---------------- BORROW REQUESTS ----------------

    public List<BorrowRecordDTO> getPendingRequests() {
        return borrowRecordRepository.findByStatus("PENDING").stream()
                .map(borrowRecordService::mapToDTO)
                .collect(Collectors.toList());
    }

    public BorrowRecordDTO acceptRequest(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        record.setStatus("BORROWED");
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));

        return borrowRecordService.mapToDTO(borrowRecordRepository.save(record));
    }

    public BorrowRecordDTO rejectRequest(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        record.setStatus("REJECTED");
        return borrowRecordService.mapToDTO(borrowRecordRepository.save(record));
    }

    // ---------------- BORROWED BOOKS ----------------

    public List<BorrowRecordDTO> getBorrowedBooks() {
        return borrowRecordRepository.findByStatus("BORROWED").stream()
                .map(borrowRecordService::mapToDTO)
                .collect(Collectors.toList());
    }

    public BorrowRecordDTO returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setStatus("RETURNED");
        record.setReturnDate(LocalDate.now());

        return borrowRecordService.mapToDTO(borrowRecordRepository.save(record));
    }

    // ---------------- OVERDUE BOOKS ----------------

    public List<BorrowRecordDTO> getOverdueBooks() {
        return borrowRecordRepository.findByStatus("OVERDUE").stream()
                .map(borrowRecordService::mapToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- PENALTIES ----------------

    public BorrowRecordDTO markAsPaid(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setStatus("PAID");
        return borrowRecordService.mapToDTO(borrowRecordRepository.save(record));
    }
}
