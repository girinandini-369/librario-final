package com.librario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "borrow_records",
        indexes = {
                @Index(name = "idx_borrow_member", columnList = "member_id"),
                @Index(name = "idx_borrow_book", columnList = "book_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relation to Member
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // relation to Book
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "borrow_date")
    private LocalDate borrowDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // BORROWED, RETURNED, PENDING, REJECTED, RENEWED, etc.

    @Column(name = "fine", nullable = false)
    @Builder.Default
    private Double fine = 0.0;

    @PrePersist
    public void prePersist() {
        if (fine == null) {
            fine = 0.0;
        }
        if (status == null) {
            status = "PENDING";
        }
    }
}
