package com.librario.controller;

import com.librario.dto.MemberDto;
import com.librario.dto.MemberRequest;
import com.librario.dto.MessageResponse;
import com.librario.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class MemberController {

    private final MemberService memberService;

    /** Register new member (admin or offline user upgrade) */
    @PostMapping("/add")
    public ResponseEntity<?> registerMember(@RequestBody MemberRequest request) {
        try {
            MemberDto saved = memberService.registerMember(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new MessageResponse("❌ Error registering member: " + e.getMessage()));
        }
    }

    /** Update existing member */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMember(
            @PathVariable Long id,
            @RequestBody MemberRequest request) {
        try {
            MemberDto updated = memberService.updateMember(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new MessageResponse("❌ Error updating member: " + e.getMessage()));
        }
    }

    /** Get all members */
    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    /** Get member by id */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(memberService.getMemberById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /** 🔹 NEW: Get member by email */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getMemberByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(memberService.getMemberByEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /** Delete member */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(new MessageResponse("✅ Member deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new MessageResponse(e.getMessage()));
        }
    }
}
