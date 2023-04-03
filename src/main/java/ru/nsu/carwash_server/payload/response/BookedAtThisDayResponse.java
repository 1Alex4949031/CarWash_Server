package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.carwash_server.models.BookedTime;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class BookedAtThisDayResponse {
    private List<BookedTime> bookedTimes;
}
