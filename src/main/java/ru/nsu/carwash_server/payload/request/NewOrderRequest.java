package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NewOrderRequest {

    private Double price;

    @NotBlank
    private String name;

    @NotNull
    private Date date;

    private boolean booked;
}
