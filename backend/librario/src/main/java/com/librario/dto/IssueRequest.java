package com.librario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueRequest {
    private Long memberId;
    private Long bookId;
}
