package com.librario.controller;

import com.librario.dto.BorrowRecordDTO;
import com.librario.service.OverdueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/overdues")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class OverdueController {

    private final OverdueService overdueService;

    @GetMapping("/check")
    public ResponseEntity<List<BorrowRecordDTO>> checkOverdues() {
        return ResponseEntity.ok(overdueService.checkOverdues());
    }
}
