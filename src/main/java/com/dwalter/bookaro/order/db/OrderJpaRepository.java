package com.dwalter.bookaro.order.db;

import com.dwalter.bookaro.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
