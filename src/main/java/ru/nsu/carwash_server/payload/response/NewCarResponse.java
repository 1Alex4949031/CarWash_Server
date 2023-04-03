package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class NewCarResponse {
    private String carNumber;
    private Long carId;
    private Long userId;
    private String carClass;
}
