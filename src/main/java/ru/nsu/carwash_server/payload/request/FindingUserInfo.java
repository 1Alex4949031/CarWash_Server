package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class FindingUserInfo {

    @NotBlank
    private String username;
}
