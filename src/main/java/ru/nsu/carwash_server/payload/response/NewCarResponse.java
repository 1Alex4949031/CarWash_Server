package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewCarResponse {
    private String carNumber;
    private Long carId;
    private Long userId;
    private String carClass;
}
