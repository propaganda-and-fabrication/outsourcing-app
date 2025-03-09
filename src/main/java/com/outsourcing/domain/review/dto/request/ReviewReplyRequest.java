package com.outsourcing.domain.review.dto.request;

import lombok.Getter;

@Getter
public class ReviewReplyRequest {
    private Long reviewId;
    private String content;
}
