package ru.nsu.carwash_server.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersStartingInOneDayRequest {
    @NotBlank
    private Date startDate;
}
