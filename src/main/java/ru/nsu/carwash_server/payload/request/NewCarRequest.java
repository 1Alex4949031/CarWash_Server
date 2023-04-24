package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class NewCarRequest {

    @NotBlank
    private String carNumber;

    @NotBlank
    private Integer carClass;
}
