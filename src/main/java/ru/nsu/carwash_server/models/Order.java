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

    private int price;

    private String wheelR;
    private boolean executed = false;

    private String comments;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_washing_link",
            joinColumns = @JoinColumn(name = "washing_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<OrdersWashing> ordersWashing = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_polishing_link",
            joinColumns = @JoinColumn(name = "polishing_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<OrdersPolishing> ordersPolishings = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_tire_link",
            joinColumns = @JoinColumn(name = "tire_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<OrdersTire> ordersTires = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    private String orderType;

    public Order(List<OrdersWashing> washingOrders, Date startTime, String administrator, String specialist,
                 int boxNumber, int bonuses, boolean executed, String comments,
                 String autoNumber, int autoType, User user, String orderType) {
        this.ordersWashing = washingOrders;
        this.startTime = startTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.bonuses = bonuses;
        this.executed = executed;
        this.comments = comments;
        this.autoNumber = autoNumber;
        this.autoType = autoType;
        this.user = user;
        this.orderType = orderType;
        this.wheelR = "wash Order";
        //setEndTimeForCarType(autoType);
    }

    public Order(List<OrdersPolishing> ordersPolishings, Date startTime, String administrator, String specialist,
                 int boxNumber, int bonuses, boolean executed, String comments,
                 String autoNumber, int autoType, User user, String orderType, int price) {
        this.ordersPolishings = ordersPolishings;
        this.startTime = startTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.bonuses = bonuses;
        this.executed = executed;
        this.comments = comments;
        this.autoNumber = autoNumber;
        this.autoType = autoType;
        this.user = user;
        this.orderType = orderType;
        this.wheelR = "polishing Order";
        this.price = price;
        //setEndTimeForCarType(autoType);
    }

    public Order(List<OrdersTire> ordersTires, Date startTime, String administrator,
                 String specialist, int boxNumber, int bonuses, boolean executed, String comments,
                 String autoNumber, int autoType, User user, String orderType, int price, String wheelR) {
        this.ordersTires = ordersTires;
        this.startTime = startTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.bonuses = bonuses;
        this.executed = executed;
        this.comments = comments;
        this.autoNumber = autoNumber;
        this.autoType = autoType;
        this.user = user;
        this.orderType = orderType;
        this.price = price;
        this.wheelR = wheelR;
        //setEndTimeForCarType(autoType);
    }

    //private void setEndTimeForCarType(int carType){
    //    int price = 0;
    //    int minutes = 15;
    //    for (var orders : ordersAdditional) {
    //        price += orders.getName().getOrderInfo().getPriceForBodyType(carType);
    //        minutes += orders.getName().getOrderInfo().getTimeForBodyType(carType);
    //    }
    //    Calendar calendar = Calendar.getInstance();
    //    calendar.setTime(this.startTime);
    //    calendar.add(Calendar.MINUTE, minutes);
//
    //    this.endTime = calendar.getTime();
//
    //    this.price = price;
    //}
}
