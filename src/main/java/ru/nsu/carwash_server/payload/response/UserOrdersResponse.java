package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserOrdersResponse {
    private Set<String> orders;
    private Long userId;
    private String username;
}
