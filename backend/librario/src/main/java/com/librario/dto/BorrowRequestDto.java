package com.librario.dto;

import lombok.Data;

@Data
public class BorrowRequestDto {
    private Long memberId;
    private Long bookId;
}
