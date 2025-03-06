package com.outsourcing.domain.review.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.order.entity.Order;
import com.outsourcing.domain.order.enums.OrderStatus;
import com.outsourcing.domain.order.repository.OrderRepository;
import com.outsourcing.domain.review.dto.request.ReviewRequest;
import com.outsourcing.domain.review.dto.request.ReviewUpdateRequest;
import com.outsourcing.domain.review.dto.response.ReviewResponse;
import com.outsourcing.domain.review.entity.Review;
import com.outsourcing.domain.review.repository.ReviewRepository;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new BaseException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.DELIVERY_COMPLETED) {
            throw new BaseException(ErrorCode.CANNOT_REVIEW);
        }

        Review review = new Review(user, order, requestDto.getRating(), requestDto.getContent());
        review.addImages(requestDto.getImageUrls());

        reviewRepository.save(review);

        return new ReviewResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getStoreReviews(Long storeId, Pageable pageable) {

        Page<Review> reviews = reviewRepository.findByOrder_Store_Id(storeId, pageable);

        return reviews.map(ReviewResponse::new);
    }

    @Transactional
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        review.updateContent(request.getContent());

        return new ReviewResponse(review);
    }
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        reviewRepository.delete(review);
    }
}
