package ru.nsu.carwash_server.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "automobiles")
@Table(name = "automobiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "carNumber"),
        })
@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class Auto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carNumber;

    private int carClass; //1 -Седан,хэтчбек;2 -Кроссовер; 3 - Кроссовер,джип

    @ManyToOne(cascade = CascadeType.MERGE)
    @ToString.Exclude
    @JsonIgnore
    private User user;

    public Auto(Long id) {
        this.id = id;
    }

    public Auto(String carNumber, int carClass, User user) {
        setCarClass(carClass);
        setCarNumber(carNumber);
        setUser(user);
    }
}
