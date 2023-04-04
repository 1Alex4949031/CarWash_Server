package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ChangeOrderRequest {
    private Long orderId;
    private Double price;
    private String name;
    private Date startTime;
    private String administrator;
    private String specialist;
    @NotNull
    private Long autoId;
    private int boxNumber;
    private int bonuses;
    private String comments;
    private boolean executed;
    private Date endTime;
}
