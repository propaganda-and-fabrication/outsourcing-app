package com.outsourcing.domain.review.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.review.dto.request.ReviewReplyRequest;
import com.outsourcing.domain.review.dto.request.ReviewReplyUpdateRequest;
import com.outsourcing.domain.review.dto.response.ReviewReplyResponse;
import com.outsourcing.domain.review.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewReplyController {
    private final ReviewReplyService reviewReplyService;

    @PostMapping("/v1/owners/reviews")
    public Response<ReviewReplyResponse> createReply(
            @AuthenticationPrincipal CustomUserDetails owner,
            @RequestBody ReviewReplyRequest request) {

        ReviewReplyResponse response = reviewReplyService.addReply(owner.getUserInfo().getId(), request);

        return Response.of(response);
    }

    @PutMapping("/v1/owner/stores/{storeId}/reviews/{reviewId}")
    public Response<ReviewReplyResponse> updateReply(
            @AuthenticationPrincipal CustomUserDetails owner,
            @PathVariable Long storeId,
            @PathVariable Long reviewId,
            @RequestBody ReviewReplyUpdateRequest request) {
        ReviewReplyResponse response = reviewReplyService.updateReviewReply(owner.getUserInfo().getId(), storeId, reviewId, request);
        return Response.of(response);
    }

    @DeleteMapping("/v1/owner/stores/{storeId}/reviews/{reviewId}")
    public Response<Void> deleteReply(
            @AuthenticationPrincipal CustomUserDetails owner,
            @PathVariable Long storeId,
            @PathVariable Long reviewId) {

        reviewReplyService.deleteReply(owner.getUserInfo().getId(), storeId, reviewId);

        return Response.of(null);
    }
}
