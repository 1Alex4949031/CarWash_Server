package ru.nsu.carwash_server.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter @Setter
@Entity(name = "automobiles")
@Table(name = "automobiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "carNumber"),
        })
public class Auto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String carNumber;
    private String carClass; //1 -Седан,хэтчбек;2 -Кроссовер; 3 - Кроссовер,джип
    @ManyToOne
    private User users;
}
