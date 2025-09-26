package com.librario.controller;

import com.librario.dto.PenaltyRequest;
import com.librario.entity.Penalty;
import com.librario.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PenaltyController {

    private final PenaltyService penaltyService;

    /**
     * Create a new penalty for a borrow record
     */
    @PostMapping
    public ResponseEntity<?> createPenalty(@RequestBody PenaltyRequest request) {
        try {
            Penalty penalty = penaltyService.createPenalty(request);
            return ResponseEntity.ok(penalty);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating penalty: " + e.getMessage());
        }
    }

    /**
     * Get all penalties
     */
    @GetMapping
    public ResponseEntity<List<Penalty>> getAllPenalties() {
        return ResponseEntity.ok(penaltyService.getAllPenalties());
    }

    /**
     * Get penalties for a specific member
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Penalty>> getPenaltiesByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(penaltyService.getPenaltiesByMember(memberId));
    }
}
