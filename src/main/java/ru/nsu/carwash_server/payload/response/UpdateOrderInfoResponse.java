package ru.nsu.carwash_server.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderInfoResponse {

    private Long orderId;

    private Double price;

    private String name;

    private Date startTime;

    private String administrator;

    private String specialist;

    private Long autoId;

    private int boxNumber;

    private int bonuses;

    private String comments;

    private boolean executed;

    private Date endTime;
}
