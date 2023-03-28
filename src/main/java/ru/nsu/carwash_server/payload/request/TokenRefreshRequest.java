package ru.nsu.carwash_server.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter @Setter
public class TokenRefreshRequest {
  @NotBlank
  private String refreshToken;
}
