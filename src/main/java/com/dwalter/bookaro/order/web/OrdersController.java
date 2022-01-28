package com.dwalter.bookaro.order.web;

import com.dwalter.bookaro.order.app.port.MaipulateOrderUseCase;
import com.dwalter.bookaro.order.app.port.QueryOrderUseCase;
import com.dwalter.bookaro.order.domain.OrderItem;
import com.dwalter.bookaro.order.domain.OrderStatus;
import com.dwalter.bookaro.order.domain.Recipient;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

import static com.dwalter.bookaro.order.app.port.QueryOrderUseCase.RichOrder;
import static org.springframework.http.HttpStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrdersController {
    private final MaipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;

    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrder.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody CreateOrderCommand command) {
        return manipulateOrder
                .placeOrder(command.toPlaceOrderCommand())
                .handle(
                        orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    URI orderUri(Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
        OrderStatus orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + command.status));
        manipulateOrder.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }

    @Data
    static class CreateOrderCommand {
        List<OrderItemCommand> items;
        RecipientCommand recipient;

        MaipulateOrderUseCase.PlaceOrderCommand toPlaceOrderCommand() {
            List<OrderItem> orderItems = items
                    .stream()
                    .map(item -> OrderItem.builder()
                            .bookId(item.bookId)
                            .quantity(item.quantity)
                            .build()
                    )
                    .toList();
            return new MaipulateOrderUseCase.PlaceOrderCommand(orderItems, recipient.toRecipient());
        }
    }

    @Data
    static class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    @Data
    static class RecipientCommand {
        String name;
        String phone;
        String street;
        String city;
        String zipCode;
        String email;

        Recipient toRecipient() {
            return new Recipient(name, phone, street, city, zipCode, email);
        }
    }

    @Data
    static class UpdateStatusCommand {
        String status;
    }
}
