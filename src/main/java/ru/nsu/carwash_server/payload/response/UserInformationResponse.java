package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.models.Role;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserInformationResponse {

    private Set<Order> orders;

    private Long id;

    private Set<Auto> cars;

    private String fullName;

    private String phone;

    private String email;

    private int bonuses;

    private Set<Role> roles;
}
