package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.carwash_server.models.helpers.OrdersPriceAndTimeInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrdersResponse {
    private List<OrdersPriceAndTimeInfo> orders;
}
