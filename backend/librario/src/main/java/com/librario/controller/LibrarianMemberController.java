package com.librario.controller;

import com.librario.dto.MemberRequest;
import com.librario.entity.Member;
import com.librario.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/librarian")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LibrarianMemberController {

    private final MemberService memberService;
      /*
    @PostMapping("/add-member")
    public ResponseEntity<Member> addMember(@RequestBody MemberRequest request) {
        Member member = memberService.createMember(request.getName(), request.getEmail(), request.getPlanId());
        return ResponseEntity.ok(member);
    } */
}
