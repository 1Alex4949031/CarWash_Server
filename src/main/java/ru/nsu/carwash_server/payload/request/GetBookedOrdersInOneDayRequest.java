package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
public class GetBookedOrdersInOneDayRequest {
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;
}
