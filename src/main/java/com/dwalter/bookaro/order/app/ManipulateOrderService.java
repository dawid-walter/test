package com.dwalter.bookaro.order.app;

import com.dwalter.bookaro.order.app.port.MaipulateOrderUseCase;
import com.dwalter.bookaro.order.db.OrderJpaRepository;
import com.dwalter.bookaro.order.domain.Order;
import com.dwalter.bookaro.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ManipulateOrderService implements MaipulateOrderUseCase {
    private final OrderJpaRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        repository.findById(id)
                .ifPresent(order -> {
                    order.setStatus(status);
                    repository.save(order);
                });
    }
}
