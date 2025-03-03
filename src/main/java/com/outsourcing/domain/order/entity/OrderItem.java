package com.outsourcing.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

//    @ManyToOne
//    @JoinColumn(name = "menu_id", nullable = false)
//    private Menu menu;

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private int quantity;
}
