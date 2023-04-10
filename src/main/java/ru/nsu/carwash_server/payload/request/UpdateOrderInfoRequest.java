package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class UpdateOrderInfoRequest {
    @NotNull
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
