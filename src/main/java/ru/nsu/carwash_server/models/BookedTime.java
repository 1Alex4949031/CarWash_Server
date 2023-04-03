package ru.nsu.carwash_server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "schedule")
@Entity
@Getter @Setter
public class BookedTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startTime;
    private Date endTime;
    private int boxNumber;
    @OneToOne
    private Order order;

    public BookedTime(Date startTime, Date endTime, int boxNumber, Order order) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.boxNumber = boxNumber;
        this.order = order;
    }

    public BookedTime() {
    }
}
