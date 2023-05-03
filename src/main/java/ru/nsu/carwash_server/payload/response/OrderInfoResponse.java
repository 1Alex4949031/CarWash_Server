package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.carwash_server.models.OrdersAdditional;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoResponse {

    private Long id;

    private List<OrdersAdditional> orders;

    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private int boxNumber;

    private String autoNumber;

    private int autoType;

    private int bonuses;

    private boolean booked;

    private boolean executed;

    private String comments;

    private Long userId;

    private int price;
}
