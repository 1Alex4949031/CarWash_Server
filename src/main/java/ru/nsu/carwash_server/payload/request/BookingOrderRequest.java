package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BookingOrderRequest {
    private Double price;
    private String name;
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
