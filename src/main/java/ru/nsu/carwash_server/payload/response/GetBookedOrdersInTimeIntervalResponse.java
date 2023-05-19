package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.carwash_server.models.DTO.OrderTimeIntervalDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBookedOrdersInTimeIntervalResponse {

    private List<OrderTimeIntervalDTO> orders;
}
