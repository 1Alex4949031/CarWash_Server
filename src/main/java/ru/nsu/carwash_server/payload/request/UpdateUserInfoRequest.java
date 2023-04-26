package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.carwash_server.models.Role;

import javax.validation.constraints.Email;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoRequest {

    @Email
    private String email;

    private String username;

    private String fullName;

    private Set<Role> role;
}
