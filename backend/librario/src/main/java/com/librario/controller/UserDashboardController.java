package com.librario.controller;

import com.librario.dto.BorrowRecordDTO;
import com.librario.dto.MemberDto;
import com.librario.entity.Book;
import com.librario.service.BookService;
import com.librario.service.BorrowRecordService;
import com.librario.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserDashboardController {

    private final BorrowRecordService borrowRecordService;
    private final MemberService memberService;
    private final BookService bookService;

    @GetMapping("/my-books/{memberId}")
    public ResponseEntity<List<BorrowRecordDTO>> getMyBooks(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getRecordsByMember(memberId));
    }

    @PostMapping("/return-book/{id}")
    public ResponseEntity<BorrowRecordDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }

    @GetMapping("/overdue-books/{memberId}")
    public ResponseEntity<List<BorrowRecordDTO>> getOverdueBooks(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getOverdueBooksByMember(memberId));
    }

    @GetMapping("/pending-books/{memberId}")
    public ResponseEntity<List<BorrowRecordDTO>> getPendingBooks(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getPendingByMember(memberId));
    }

    @GetMapping("/membership/{memberId}")
    public ResponseEntity<MemberDto> getMembershipDetails(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

    @GetMapping("/available-books")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> books = bookService.getAllBooks().stream()
                .filter(b -> b.getAvailableCopies() != null && b.getAvailableCopies() > 0)
                .toList();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search-books")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }
}
