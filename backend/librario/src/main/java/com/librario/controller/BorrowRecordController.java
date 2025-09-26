package com.librario.controller;

import com.librario.dto.BorrowRecordDTO;
import com.librario.dto.BorrowRequestDto;
import com.librario.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    // Request a book (PENDING)
    @PostMapping("/request")
    public ResponseEntity<BorrowRecordDTO> requestBook(@RequestBody BorrowRequestDto requestDto) {
        return ResponseEntity.ok(
                borrowRecordService.requestBook(requestDto.getMemberId(), requestDto.getBookId())
        );
    }

    // Borrow a book (immediate)
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecordDTO> borrowBook(@RequestBody BorrowRequestDto requestDto) {
        return ResponseEntity.ok(
                borrowRecordService.createBorrow(requestDto.getMemberId(), requestDto.getBookId())
        );
    }

    // Return
    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowRecordDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }

    // Approve (librarian)
    @PutMapping("/{id}/approve")
    public ResponseEntity<BorrowRecordDTO> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.approveRequest(id));
    }

    // Reject
    @PutMapping("/{id}/reject")
    public ResponseEntity<BorrowRecordDTO> rejectRequest(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.rejectRequest(id));
    }

    // Pay fine (manual)
    @PutMapping("/{id}/pay-fine")
    public ResponseEntity<BorrowRecordDTO> payFine(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.payFine(id));
    }

    // Get all borrow records (admin)
    @GetMapping
    public ResponseEntity<List<BorrowRecordDTO>> getAllRecords() {
        return ResponseEntity.ok(borrowRecordService.getAllRecords());
    }

    // Member specific - all records
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BorrowRecordDTO>> getRecordsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getRecordsByMember(memberId));
    }

    // Member specific - borrowed (active)
    @GetMapping("/member/{memberId}/borrowed")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowedByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getBorrowedByMember(memberId));
    }

    // Member specific - overdue
    @GetMapping("/member/{memberId}/overdue")
    public ResponseEntity<List<BorrowRecordDTO>> getOverdueByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getOverdueBooksByMember(memberId));
    }

    // Member specific - pending
    @GetMapping("/member/{memberId}/pending")
    public ResponseEntity<List<BorrowRecordDTO>> getPendingByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getPendingByMember(memberId));
    }

    // Member specific - returned history
    @GetMapping("/member/{memberId}/returned")
    public ResponseEntity<List<BorrowRecordDTO>> getReturnedByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getReturnedByMember(memberId));
    }

    // Librarian views
    @GetMapping("/pending")
    public ResponseEntity<List<BorrowRecordDTO>> getAllPendingRequests() {
        return ResponseEntity.ok(borrowRecordService.getAllPendingRequests());
    }

    @GetMapping("/overdue/all")
    public ResponseEntity<List<BorrowRecordDTO>> getAllOverdueRecords() {
        return ResponseEntity.ok(borrowRecordService.getAllOverdueRecords());
    }
}
