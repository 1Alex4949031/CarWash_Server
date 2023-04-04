package ru.nsu.carwash_server.models;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.carwash_server.models.constants.ERole;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ERole name;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }

}
