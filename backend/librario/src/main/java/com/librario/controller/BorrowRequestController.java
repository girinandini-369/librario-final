package com.librario.controller;

import com.librario.entity.BorrowRequest;
import com.librario.dto.BorrowRecordDTO;
import com.librario.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class BorrowRequestController {

    private final BorrowRequestService requestService;

    @PostMapping("/{memberId}/{bookId}")
    public BorrowRequest createRequest(@PathVariable Long memberId, @PathVariable Long bookId) {
        return requestService.createRequest(memberId, bookId);
    }

    @PostMapping("/approve/{id}")
    public BorrowRecordDTO approveRequest(@PathVariable Long id) {
        return requestService.approveRequest(id);
    }

    @PostMapping("/reject/{id}")
    public BorrowRequest rejectRequest(@PathVariable Long id) {
        return requestService.rejectRequest(id);
    }

    @GetMapping
    public List<BorrowRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/pending")
    public List<BorrowRequest> getPendingRequests() {
        return requestService.getPendingRequests();
    }
}
