package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import ru.nsu.carwash_server.models.helpers.TimeIntervals;

import java.util.List;

@Data
@Setter
@AllArgsConstructor
public class TimeAndPriceAndFreeTimeResponse {
    Integer price;
    Integer time;
    List<TimeIntervals> availableTime;
}
