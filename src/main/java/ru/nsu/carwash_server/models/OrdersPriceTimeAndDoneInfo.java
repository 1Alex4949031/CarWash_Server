package ru.nsu.carwash_server.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OrdersPriceTimeAndDoneInfo {
    private String orderType;

    private int price;

    @JsonFormat(timezone="Asia/Novosibirsk")
    private Date startTime;

    private boolean executed;
}