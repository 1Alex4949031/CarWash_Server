package ru.nsu.carwash_server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "orders")
@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private String name;
    private Date startTime;
    private Date endTime;
    private String administrator;
    private String specialist;
    private int boxNumber;
    private int bonuses;
    private boolean booked;
    private boolean executed = false;
    private String comments;
    @OneToOne
    @JoinColumn(name = "auto_id", referencedColumnName = "id")
    private Auto auto;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Order(String name, double price, Date startTime, Date endTime, String administrator, String specialist,
                 int boxNumber, int bonuses, boolean booked, boolean executed, String comments,
                 Auto auto, User user) {
        this.name = name;
        this.price = price;
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

    public Order(String name, double price, Date startTime) {
        this.name = name;
        this.price = price;
        this.startTime = startTime;
    }

    public String startAndEndTimeToString(){
        return (this.getId() + " " + this.startTime
                + " " + this.endTime + " " + this.boxNumber);
    }
}
