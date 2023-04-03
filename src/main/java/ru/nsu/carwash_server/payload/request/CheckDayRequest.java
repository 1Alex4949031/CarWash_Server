package ru.nsu.carwash_server.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter @Setter
public class CheckDayRequest {
    @NotNull
    private Date date;
    private int boxNumber;
}
