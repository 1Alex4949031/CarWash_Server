package ru.nsu.carwash_server.payload.request;


import lombok.Data;

import javax.validation.constraints.*;

@Data
public class TokenRefreshRequest {
  @NotBlank
  private String refreshToken;
}
