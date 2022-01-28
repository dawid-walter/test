package com.dwalter.bookaro.order.app;

import com.dwalter.bookaro.catalog.db.BookJpaRepository;
import com.dwalter.bookaro.catalog.domain.model.Book;
import com.dwalter.bookaro.order.app.port.QueryOrderUseCase;
import com.dwalter.bookaro.order.db.OrderJpaRepository;
import com.dwalter.bookaro.order.domain.Order;
import com.dwalter.bookaro.order.domain.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository repository;
    private final BookJpaRepository catalogRepository;

    @Override
    public List<RichOrder> findAll() {
            return repository.findAll()
                    .stream()
                    .map(this::toRichOrder)
                    .toList();
        }

        @Override
        public Optional<RichOrder> findById(Long id) {
            return repository.findById(id).map(this::toRichOrder);
        }

        private RichOrder toRichOrder(Order order) {
            List<RichOrderItem> richItems = toRichItems(order.getItems());
            return new RichOrder(
                    order.getId(),
                    order.getStatus(),
                    richItems,
                    order.getRecipient(),
                    order.getCreatedAt()
            );
        }

        private List<RichOrderItem> toRichItems(List<OrderItem> items) {
            return items.stream()
                    .map(item -> {
                        Book book = catalogRepository
                                .findById(item.getBookId())
                                .orElseThrow(() -> new IllegalStateException("Unable to find book with ID: " + item.getBookId()));
                        return new RichOrderItem(book, item.getQuantity());
                    })
                    .toList();
        }
}
