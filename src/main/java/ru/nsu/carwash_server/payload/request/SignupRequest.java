package ru.nsu.carwash_server.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter @Setter
public class SignupRequest {
    @NotBlank
    private String username;
    private Set<String> role;
    @NotBlank
    private String password;
}
