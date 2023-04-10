package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class SignupRequest {
    @NotNull
    private String username;
    private Set<String> role;
    @NotNull
    private String password;
}
