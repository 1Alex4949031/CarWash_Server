package ru.nsu.carwash_server.payload.request;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class OrdersArrayPriceTimeRequest {
    String orderType;
    ArrayList<String> orders;
    String wheelR;
    int bodyType;
}
