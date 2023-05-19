package ru.nsu.carwash_server.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTimeIntervalDTO {
    @Column(name = "start_time")
    Date start_time;

    @Column(name = "end_time")
    Date end_time;

    @Column(name = "box_number")
    Integer box_number;

}