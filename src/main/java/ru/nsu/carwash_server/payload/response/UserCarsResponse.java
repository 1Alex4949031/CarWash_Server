package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.User;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserCarsResponse {

    private Set<Auto> autoList;

    private User user;
}
