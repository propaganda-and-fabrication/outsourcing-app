package com.outsourcing.domain.review.repository;

import com.outsourcing.domain.review.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
}
