package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class NewOrderRequest {
    private Double price;
    @NotNull
    private String name;
    @NotNull
    private Date date;
    private boolean booked;

}
