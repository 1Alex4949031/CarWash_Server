package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.carwash_server.models.Order;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor
public class UserOrdersResponse {
    private Set<Order> orders;
    private String username;
}
