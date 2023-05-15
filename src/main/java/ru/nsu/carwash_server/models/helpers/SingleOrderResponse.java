package ru.nsu.carwash_server.models.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
public class SingleOrderResponse {

    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private String autoNumber;

    private int autoType;

    private int boxNumber;

    private int bonuses;

    private int price;

    private String wheelR;
    private boolean executed;

    private String comments;

    List<String> orders;

    private String userNumber;

    private String orderType;
}

