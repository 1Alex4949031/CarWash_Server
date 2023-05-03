package ru.nsu.carwash_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private String autoNumber;

    private int autoType;

    private int boxNumber;

    private int bonuses;

    private boolean booked;

    private int price;

    private boolean executed = false;

    private String comments;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_extra_link",
            joinColumns = @JoinColumn(name = "extras_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<OrdersAdditional> ordersAdditional = new ArrayList<>();


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    public Order(List<OrdersAdditional> extraOrders,Date startTime, String administrator, String specialist,
                 int boxNumber, int bonuses, boolean booked, boolean executed, String comments,
                 String autoNumber, int autoType, User user) {
        this.ordersAdditional = extraOrders;
        this.startTime = startTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.bonuses = bonuses;
        this.booked = booked;
        this.executed = executed;
        this.comments = comments;
        this.autoNumber = autoNumber;
        this.autoType = autoType;
        this.user = user;
        setEndTimeForCarType(autoType);
    }

    public Order( List<OrdersAdditional> extraOrders,Date startTime, Date endTime, String administrator, String specialist,
                 int boxNumber, String autoNumber, int autoType, String comments) {
        this.ordersAdditional = extraOrders;
        this.startTime = startTime;
        this.endTime = endTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.bonuses = 0;
        this.autoType = autoType;
        this.autoNumber = autoNumber;
        this.executed = false;
        this.booked = true;
        this.comments = comments;
    }

    private void setEndTimeForCarType(int carType){
        int price = 0;
        int minutes = 15;
        for (var orders : ordersAdditional) {
            price += orders.getName().getOrderInfo().getPriceForBodyType(carType);
            minutes += orders.getName().getOrderInfo().getTimeForBodyType(carType);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startTime);
        calendar.add(Calendar.MINUTE, minutes);

        this.endTime = calendar.getTime();

        this.price = price;
    }
}
