package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.carwash_server.models.Auto;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor
public class UserCarsResponse {
    private Set<Auto> autoList;
    private Long userId;
    private String username;
}
