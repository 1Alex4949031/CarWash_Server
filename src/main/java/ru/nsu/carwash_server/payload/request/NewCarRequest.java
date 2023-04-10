package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class NewCarRequest {
    @NotNull
    private String carNumber;
    @NotNull
    private String carClass;
}
