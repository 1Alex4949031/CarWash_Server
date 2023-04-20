package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class BookingOrderRequest {

    private Double price;

    @NotBlank
    private String name;

    @NotNull
    private Date startTime;

    private String administrator;

    private String specialist;

    private Long autoId;

    private int boxNumber;

    private int bonuses;

    private String comments;

    private boolean executed;

    private Date endTime;
}
