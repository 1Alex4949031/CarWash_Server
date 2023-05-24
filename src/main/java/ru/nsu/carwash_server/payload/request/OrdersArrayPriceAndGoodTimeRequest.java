package ru.nsu.carwash_server.payload.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class OrdersArrayPriceAndGoodTimeRequest {

    String orderType;

    ArrayList<String> orders;

    String wheelR;

    int bodyType;

    //@JsonFormat(timezone="Asia/Novosibirsk")
    private Date startTime;

    //@JsonFormat(timezone="Asia/Novosibirsk")
    private Date endTime;
}
