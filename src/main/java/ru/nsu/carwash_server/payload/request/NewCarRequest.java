package ru.nsu.carwash_server.payload.request;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NewCarRequest {
    private String carNumber;
    private String carClass;}
