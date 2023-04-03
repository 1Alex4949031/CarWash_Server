package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class NewOrderRequest {
    private Double price;

    private String name;

    private Date date;

    private boolean booked;

}
