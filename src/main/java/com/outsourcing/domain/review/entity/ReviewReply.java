package com.outsourcing.domain.review.entity;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review_replies")
public class ReviewReply extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review; // 부모 리뷰

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 500)
    private String content;

    public ReviewReply(Review review, User owner, String content) {
        this.review = review;
        this.owner = owner;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
