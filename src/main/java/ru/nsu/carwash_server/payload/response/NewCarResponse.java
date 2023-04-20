package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NewCarResponse {

    private String carNumber;

    private Long carId;

    private Long userId;

    private String carClass;
}
