package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter @Setter
@AllArgsConstructor
@Builder
public class NewOrderRequest {
    private Double price;

    private String name;

    private Date date;

    private boolean booked;

}
