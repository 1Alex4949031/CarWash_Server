package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.carwash_server.models.Order;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@ToString
public class UserOrdersResponse {
    private Set<String> orders;
    private Long userId;
    private String username;
}
