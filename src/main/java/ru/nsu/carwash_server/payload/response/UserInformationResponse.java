package ru.nsu.carwash_server.payload.response;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.models.Role;

import java.util.Set;

@Getter
@Setter
public class UserInformationResponse {
    private Set<Order> orders;
    private Long id;
    private Set<Auto> cars;
    private String fullName;
    private String phone;
    private String email;
    private int bonuses;
    private Set<Role> roles;

    public UserInformationResponse(Set<Order> orders, Long id, Set<Auto> cars,
                            String fullName, String phone, String email, int bonuses, Set<Role> roles) {
        setBonuses(bonuses);
        setId(id);
        setCars(cars);
        setEmail(email);
        setPhone(phone);
        setRoles(roles);
        setFullName(fullName);
        setOrders(orders);
    }
}
