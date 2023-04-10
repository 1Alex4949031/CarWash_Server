package ru.nsu.carwash_server.payload.request;


import javax.validation.constraints.NotNull;

public class TokenRefreshRequest {
  @NotNull
  private String refreshToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
