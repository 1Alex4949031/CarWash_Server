package ru.nsu.carwash_server.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoResponse {

    private Long id;

    private List<String> orders;

    @JsonFormat(timezone="Asia/Novosibirsk")
    private Date startTime;

    @JsonFormat(timezone="Asia/Novosibirsk")
    private Date endTime;

    private String administrator;

    private String specialist;

    private int boxNumber;

    private String autoNumber;

    private int autoType;

    private int bonuses;

    private boolean executed;

    private String comments;

    private Long userId;

    private int price;

    private String orderType;

    private String wheelR;

    public OrderInfoResponse(Long id, List<String> orders, Date startTime, Date endTime, String administrator,
                             String specialist, int boxNumber, String autoNumber, int autoType, int bonuses, boolean executed,
                             String comments, Long userId, int price, String orderType, String wheelR) {
        this.id = id;
        this.orders = orders;
        this.startTime = startTime;
        this.endTime = endTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.autoNumber = autoNumber;
        this.autoType = autoType;
        this.bonuses = bonuses;
        this.executed = executed;
        this.comments = comments;
        this.userId = userId;
        this.price = price;
        this.orderType = orderType;
        this.wheelR = wheelR;
    }

    public OrderInfoResponse(Long id, List<String> orders, Date startTime, Date endTime, String administrator, String specialist, int boxNumber,
                             String autoNumber, int autoType, int bonuses, boolean executed, String comments,
                             int price, String orderType, String wheelR, String userContacts) {
        this.id = id;
        this.orders = orders;
        this.startTime = startTime;
        this.endTime = endTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.autoNumber = autoNumber;
        this.autoType = autoType;
        this.bonuses = bonuses;
        this.executed = executed;
        this.comments = comments;
        this.price = price;
        this.orderType = orderType;
        this.wheelR = wheelR;
        this.userContacts = userContacts;
    }

    private String userContacts;
}
