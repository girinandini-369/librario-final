package com.librario.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "overdue")
public class Overdue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "days_overdue", nullable = false)
    private int daysOverdue;  // 👈 match DB column

    @Column(name = "days_late", nullable = false)
    private int daysLate;

    @Column(nullable = false)
    private Double fine;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public int getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(int daysOverdue) { this.daysOverdue = daysOverdue; }

    public int getDaysLate() { return daysLate; }
    public void setDaysLate(int daysLate) { this.daysLate = daysLate; }

    public Double getFine() { return fine; }
    public void setFine(Double fine) { this.fine = fine; }
}
