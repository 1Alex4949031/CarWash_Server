package ru.nsu.carwash_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.carwash_server.models.constants.EOrderAdditional;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "orders_extra")
@NoArgsConstructor
@ToString
@Getter @Setter
@AllArgsConstructor
public class OrdersAdditional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EOrderAdditional name;

    public OrdersAdditional(EOrderAdditional eOrderAdditional) {
        this.name = eOrderAdditional;
    }
}
