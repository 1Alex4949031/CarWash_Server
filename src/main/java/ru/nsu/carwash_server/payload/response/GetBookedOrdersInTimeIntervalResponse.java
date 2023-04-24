package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.carwash_server.models.Order;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBookedOrdersInTimeIntervalResponse {

    private Set<Order> orders;
}
