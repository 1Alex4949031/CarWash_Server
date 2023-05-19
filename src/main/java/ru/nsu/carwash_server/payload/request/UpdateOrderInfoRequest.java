package ru.nsu.carwash_server.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UpdateOrderInfoRequest {

    @NotNull
    private Long orderId;

    private String userPhone;

    private String orderType;

    private Integer price = null;

    private Date startTime = null;

    private String administrator = null;

    private String autoNumber = null;

    private Integer autoType = null;

    private String specialist = null;

    private Integer boxNumber = null;

    private Integer bonuses = null;

    private String comments;

    private boolean executed;

    private Date endTime = null;
}
