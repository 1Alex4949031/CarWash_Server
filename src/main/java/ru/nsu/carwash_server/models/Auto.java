package ru.nsu.carwash_server.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@AllArgsConstructor
@Entity(name = "automobiles")
@Table(name = "automobiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "carNumber"),
        })
@NoArgsConstructor
public class Auto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String carNumber;
    private String carClass; //1 -Седан,хэтчбек;2 -Кроссовер; 3 - Кроссовер,джип
    @ManyToOne
    private User users;

    public Auto(Long id) {
        this.id = id;
    }

    public Auto(String carNumber, String carClass, User user) {
        setCarClass(carClass);
        setCarNumber(carNumber);
        setUsers(user);
    }
}
