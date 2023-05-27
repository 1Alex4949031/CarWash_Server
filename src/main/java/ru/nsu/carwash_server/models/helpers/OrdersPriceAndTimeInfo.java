package ru.nsu.carwash_server.models.helpers;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OrdersPriceAndTimeInfo{
    private String orderType;

    private int price;

    @JsonFormat(timezone="Asia/Novosibirsk")
    private Date startTime;
}