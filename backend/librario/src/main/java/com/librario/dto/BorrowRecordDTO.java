package com.librario.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BorrowRecordDTO {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long bookId;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private Double fine;
    private Long transactionId;
}
