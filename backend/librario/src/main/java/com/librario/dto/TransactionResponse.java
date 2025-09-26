package com.librario.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long memberId;
    private Long bookId;
    private String bookTitle;
    private String memberName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Double fine;
    private String status;
}
