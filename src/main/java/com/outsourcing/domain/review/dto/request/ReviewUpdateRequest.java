package com.outsourcing.domain.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewUpdateRequest {
    private String content;

    public ReviewUpdateRequest(String content) {
        this.content = content;
    }
}
