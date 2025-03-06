package com.outsourcing.domain.review.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.review.dto.request.ReviewRequest;
import com.outsourcing.domain.review.dto.request.ReviewUpdateRequest;
import com.outsourcing.domain.review.dto.response.ReviewResponse;
import com.outsourcing.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/v1/customers/reviews")
    public Response<ReviewResponse> createReview(@AuthenticationPrincipal CustomUserDetails user, @RequestBody ReviewRequest requestDto) {

        ReviewResponse response = reviewService.createReview(user.getUserInfo().getId(), requestDto);

        return Response.of(response);
    }

    // 사용자 에 대한 리뷰 보기
    @GetMapping("/v1/customers/{userId}/reviews")
    public Response<Page<ReviewResponse>> getUserReviews(
            @PathVariable Long userId, Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.getUserReviews(userId, pageable);

        return Response.of(reviews);
    }

    // 해당 가게에 대한 리뷰 모두 보기
    @GetMapping("/v1/customers/store/{storeId}/reviews")
    public Response<Page<ReviewResponse>> getStoreReviews(
            @PathVariable Long storeId, Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.getStoreReviews(storeId, pageable);

        return Response.of(reviews);
    }

    @PutMapping("/v1/reviews/{reviewId}")
    public Response<ReviewResponse> updateReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reviewId, @RequestBody ReviewUpdateRequest request) {

        ReviewResponse response = reviewService.updateReview(user.getUserInfo().getId(), reviewId, request);

        return Response.of(response);
    }

    @DeleteMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable Long reviewId) {

        reviewService.deleteReview(user.getUserInfo().getId(), reviewId);

        return ResponseEntity.noContent().build();
    }
}
