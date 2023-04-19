package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.nsu.carwash_server.models.Order;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserOrdersResponse {
    private Set<Order> orders;
}
