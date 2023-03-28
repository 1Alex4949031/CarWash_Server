package ru.nsu.carwash_server.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class NewOrderRequest {

    @NotNull
    @Getter
    @Setter
    private Double price;

    @NotBlank
    @Getter
    @Setter
    private String name;

    private Date date;

    @Getter
    @Setter
    private boolean booked;

    public Date getDate() {
        return date;
    }

}
