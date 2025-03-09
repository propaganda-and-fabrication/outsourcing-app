package com.outsourcing.domain.review.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewRequest {
    private Long orderId;
    private int rating;
    private String content;
    private List<String> imageUrls;
}
