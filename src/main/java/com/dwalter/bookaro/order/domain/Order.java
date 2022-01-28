package com.dwalter.bookaro.order.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId")
    private List<OrderItem> items;
    transient private Recipient recipient;
    private LocalDateTime createdAt;
}
