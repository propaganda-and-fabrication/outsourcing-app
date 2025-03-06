package com.outsourcing.domain.review.dto.response;

import com.outsourcing.domain.review.entity.ReviewReply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewReplyResponse {
    private Long id;
    private String ownerName; // 사장님 이름
    private String content;

    public ReviewReplyResponse(ReviewReply reply) {
        this.id = reply.getId();
        this.ownerName = reply.getOwner().getName();
        this.content = reply.getContent();
    }
}
