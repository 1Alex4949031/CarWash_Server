package ru.nsu.carwash_server.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter @Setter
public class BookingOrderRequest {
    @NotNull
    private Long orderId;
    @NotNull
    private Long userId;
    private Double price;
    private Date date;
    private String administrator;
    private String specialist;
    @NotNull
    private Long autoId;
    private int boxNumber;
    private int bonuses;
}
