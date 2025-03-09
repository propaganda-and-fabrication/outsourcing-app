package com.outsourcing.domain.review.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.review.dto.request.ReviewReplyRequest;
import com.outsourcing.domain.review.dto.request.ReviewReplyUpdateRequest;
import com.outsourcing.domain.review.dto.response.ReviewReplyResponse;
import com.outsourcing.domain.review.dto.response.ReviewResponse;
import com.outsourcing.domain.review.entity.Review;
import com.outsourcing.domain.review.entity.ReviewReply;
import com.outsourcing.domain.review.repository.ReviewReplyRepository;
import com.outsourcing.domain.review.repository.ReviewRepository;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReviewReplyService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReviewReplyResponse addReply(Long ownerId, ReviewReplyRequest request) {

        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_RIVIEW));

        User owner =  userRepository.findById(ownerId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!review.getOrder().getStore().getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        if (review.getReviewReply() != null) {
            throw new BaseException(ErrorCode.REPLY_ALREADY_EXISTS);
        }

        ReviewReply reply = new ReviewReply(review, owner, request.getContent());
        reviewReplyRepository.save(reply);

        return new ReviewReplyResponse(reply);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getStoreReviews(Long ownerId, Long storeId, Pageable pageable) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        return reviewRepository.findByOrder_Store_Id(storeId, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public ReviewReplyResponse updateReviewReply(Long ownerId, Long storeId, Long reviewId, ReviewReplyUpdateRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_RIVIEW));

        ReviewReply reply = review.getReviewReply();
        if (reply == null) {
            throw new IllegalArgumentException("No reply found for this review");
        }

        if (!review.getOrder().getStore().getId().equals(storeId)) {
            throw new BaseException(ErrorCode.NOT_FOUND_REPLY_REVIEW);
        }

        if (!review.getOrder().getStore().getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        reply.updateContent(request.getContent());

        return new ReviewReplyResponse(reply);
    }

    @Transactional
    public void deleteReply(Long ownerId, Long storeId, Long reviewId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

        // 사장님이 해당 가게의 주인인지 검증
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_RIVIEW));

        ReviewReply reply = review.getReviewReply();
        if (reply == null) {
            throw new IllegalArgumentException("Reply not found");
        }

        reviewReplyRepository.delete(reply);
    }
}
