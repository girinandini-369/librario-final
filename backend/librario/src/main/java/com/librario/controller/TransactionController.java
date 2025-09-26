package com.librario.controller;

import com.librario.dto.IssueRequest;
import com.librario.dto.ReturnRequest;
import com.librario.dto.RenewRequest;
import com.librario.dto.TransactionResponse;
import com.librario.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TransactionController {

    private final TransactionService transactionService;

    // 📌 Issue Book
    @PostMapping("/issue")
    public ResponseEntity<TransactionResponse> issueBook(@RequestBody IssueRequest request) {
        return ResponseEntity.ok(
                transactionService.issueBook(request.getMemberId(), request.getBookId())
        );
    }

    // 📌 Return Book
    @PostMapping("/return")
    public ResponseEntity<TransactionResponse> returnBook(@RequestBody ReturnRequest request) {
        return ResponseEntity.ok(
                transactionService.returnBook(request.getTransactionId())
        );
    }

    // 📌 Renew Book
    @PostMapping("/renew")
    public ResponseEntity<TransactionResponse> renewBook(@RequestBody RenewRequest request) {
        return ResponseEntity.ok(
                transactionService.renewBook(request.getTransactionId(), request.getExtraDays())
        );
    }

    // 📌 Get All Transactions by Member
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<TransactionResponse>> getByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(transactionService.getTransactionsByMember(memberId));
    }

    // 📌 Get Overdue Transactions by Member
    @GetMapping("/member/{memberId}/overdue")
    public ResponseEntity<List<TransactionResponse>> getOverdueByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(transactionService.getOverdueTransactionsByMember(memberId));
    }

    // 📌 Get Returned Transactions by Member
    @GetMapping("/member/{memberId}/returned")
    public ResponseEntity<List<TransactionResponse>> getReturnedByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(transactionService.getReturnedTransactionsByMember(memberId));
    }

    // 📌 Get All Transactions by Book
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<TransactionResponse>> getByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBook(bookId));
    }

    // 📌 Get All Transactions
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // 📌 Get Overdue Transactions (all members)
    @GetMapping("/overdue")
    public ResponseEntity<List<TransactionResponse>> getOverdue() {
        return ResponseEntity.ok(transactionService.getOverdueTransactions());
    }

    // 📌 Mark Fine Paid
    @PutMapping("/{id}/mark-fine-paid")
    public ResponseEntity<TransactionResponse> markFinePaid(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.markFinePaid(id));
    }

    // 📌 Mark Returned
    @PutMapping("/{id}/mark-returned")
    public ResponseEntity<TransactionResponse> markReturned(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.markReturned(id));
    }
}
