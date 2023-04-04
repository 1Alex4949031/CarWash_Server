package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class GetBookedOrdersInOneDayRequest {
    private Date startTime;
    private Date endTime;
}
