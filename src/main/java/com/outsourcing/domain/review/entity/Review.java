package com.outsourcing.domain.review.entity;


import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.order.entity.Order;
import com.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 500)
    private String content;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewReply reply;

    public Review(User user, Order order, int rating, String content) {
        this.user = user;
        this.order = order;
        this.rating = rating;
        this.content = content;
    }

    public void addImages(List<String> imageUrls) {
        imageUrls.forEach(url -> this.images.add(new Image(this, url)));
    }

    public ReviewReply getReviewReply() {
        return reply;
    }

    public void addReviewReply(ReviewReply reply) {
        this.reply = reply;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
