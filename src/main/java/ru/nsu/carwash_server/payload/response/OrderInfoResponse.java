package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.carwash_server.models.OrdersAdditional;
import ru.nsu.carwash_server.models.constants.EOrderMain;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoResponse {

    private Long id;

    private EOrderMain mainOrder;

    private List<OrdersAdditional> extraOrders;

    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private int boxNumber;

    private int bonuses;

    private boolean booked;

    private boolean executed;

    private String comments;

    private Long userId;
}
