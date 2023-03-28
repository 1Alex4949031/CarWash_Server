package ru.nsu.carwash_server.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;



@Entity
@Setter @Getter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double price;

    @NotBlank
    private String name;
    private Date date;
    private String administrator;
    private String specialist;
    private int boxNumber;
    private int bonuses;
    private boolean booked;
    private boolean executed;
    @OneToOne
    @JoinColumn(name = "auto_id", referencedColumnName = "id")
    private Auto auto;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Order() {
    }

    public Order(String name, double price, Date date) {
        this.name = name;
        this.price = price;
        this.date = date;
    }
}