package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserCarsResponse {
    private Set<String> autoList;
    private Long userId;
    private String username;
}
