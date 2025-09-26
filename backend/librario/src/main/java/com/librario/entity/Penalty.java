package com.librario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "penalty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to borrow record (entity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at")
    private LocalDate createdAt;
}
