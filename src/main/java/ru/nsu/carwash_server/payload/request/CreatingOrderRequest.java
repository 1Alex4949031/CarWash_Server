package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatingOrderRequest {

    private String mainOrder;

    private List<String> extraOrders;

    @NotNull
    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private String autoNumber;

    private int boxNumber;

    private String comments;

    private String fullName;

    private boolean executed;
}