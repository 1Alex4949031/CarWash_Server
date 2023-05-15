package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoRequest {

    @Email
    private String email = null;

    private String username = null;

    private String fullName = null;

    private Set<String> roles = null;
}
