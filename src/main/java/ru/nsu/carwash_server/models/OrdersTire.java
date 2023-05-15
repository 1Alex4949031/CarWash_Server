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
@Table(name = "orders_tire")
@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class OrdersTire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    private String name;

    private int price_r_13;

    private int price_r_14;

    private int price_r_15;

    private int price_r_16;

    private int price_r_17;

    private int price_r_18;

    private int price_r_19;

    private int price_r_20;

    private int price_r_21;

    private int price_r_22;

    private String role;
}
