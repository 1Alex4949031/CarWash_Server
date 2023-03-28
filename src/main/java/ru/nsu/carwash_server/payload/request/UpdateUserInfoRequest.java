package ru.nsu.carwash_server.payload.request;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.carwash_server.models.Role;

import javax.validation.constraints.Email;
import java.util.Set;

@Getter @Setter
public class UpdateUserInfoRequest {
    @Email
    private String email;
    private Long id;
    private String username;
    private String fullName;
    private Set<Role> role;
}
