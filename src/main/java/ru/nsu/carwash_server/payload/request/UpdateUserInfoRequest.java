package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.nsu.carwash_server.models.Role;

import javax.validation.constraints.Email;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class UpdateUserInfoRequest {

    @Email
    private String email;

    private String username;

    private String fullName;

    private Set<Role> role;
}
