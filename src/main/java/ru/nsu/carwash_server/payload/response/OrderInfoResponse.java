package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OrderInfoResponse {

    private Long id;

    private Double price;

    private String name;

    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private int boxNumber;

    private int bonuses;

    private boolean booked;

    private boolean executed;

    private String comments;

    private Long userId;
}
