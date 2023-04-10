package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class LoginRequest {
	@NotNull
	private String username;
	@NotNull
	private String password;
}
