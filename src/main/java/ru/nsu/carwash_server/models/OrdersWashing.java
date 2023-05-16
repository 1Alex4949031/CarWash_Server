package ru.nsu.carwash_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "orders_washing")
@NoArgsConstructor
@ToString
@Getter @Setter
@AllArgsConstructor
public class OrdersWashing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    private String name;

    private int priceFirstType;

    private int priceSecondType;

    private int priceThirdType;

    private int timeFirstType;

    private int timeSecondType;

    private int timeThirdType;

    private String role;

    private String includedIn; //включена в какую-то услугу

    private String associatedOrder; // идём вместе с каким-то заказом

}
