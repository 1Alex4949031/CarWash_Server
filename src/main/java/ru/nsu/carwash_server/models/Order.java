package ru.nsu.carwash_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nsu.carwash_server.models.constants.EOrderMain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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

    @Enumerated(EnumType.STRING)
    private EOrderMain eOrderMain;

    private Date startTime;

    private Date endTime;

    private String administrator;

    private String specialist;

    private int boxNumber;

    private int bonuses;

    private boolean booked;

    private boolean executed = false;

    private String comments;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "auto_id", referencedColumnName = "id")
    private Auto auto;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_extra_link",
            joinColumns = @JoinColumn(name = "extras_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<OrdersAdditional> ordersAdditional = new ArrayList<>();


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    public Order(EOrderMain mainOrder, List<OrdersAdditional> extraOrders,Date startTime, Date endTime, String administrator, String specialist,
                 int boxNumber, int bonuses, boolean booked, boolean executed, String comments,
                 Auto auto, User user) {
        this.eOrderMain = mainOrder;
        this.ordersAdditional = extraOrders;
        this.startTime = startTime;
        this.endTime = endTime;
        this.administrator = administrator;
        this.specialist = specialist;
        this.boxNumber = boxNumber;
        this.bonuses = bonuses;
        this.booked = booked;
        this.executed = executed;
        this.comments = comments;
        this.auto = auto;
        this.user = user;
    }
}
