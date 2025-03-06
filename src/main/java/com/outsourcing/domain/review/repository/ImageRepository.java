package com.outsourcing.domain.review.repository;

import com.outsourcing.domain.review.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
