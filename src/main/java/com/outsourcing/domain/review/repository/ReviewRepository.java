package com.outsourcing.domain.review.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.outsourcing.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByOrderId(Long orderId);

    Page<Review> findByUserId(Long orderId, Pageable pageable);

    Page<Review> findByOrder_Store_Id(Long storeId, Pageable pageable);
}
