package ru.nsu.carwash_server.models.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OrdersPriceAndTimeInfo{
    private String orderType;

    private int price;

    private Date startTime;
}