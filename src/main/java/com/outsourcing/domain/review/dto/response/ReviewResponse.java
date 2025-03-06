package com.outsourcing.domain.review.dto.response;

import com.outsourcing.domain.review.entity.Image;
import com.outsourcing.domain.review.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String userName;
    private int rating;
    private String content;
    private List<String> imageUrls;
    private ReviewReplyResponse reply; // 사장님 대댓 추가

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userName = review.getUser().getName();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.imageUrls = review.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        // 사장님이 대댓을 달았으면 추가
        this.reply = review.getReviewReply() != null ? new ReviewReplyResponse(review.getReviewReply()) : null;
    }

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review);
    }

}