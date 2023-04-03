package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.payload.request.CheckDayRequest;
import ru.nsu.carwash_server.payload.response.BookedAtThisDayResponse;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.ScheduleRepository;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    OrdersRepository ordersRepository;

    @GetMapping("getOrderByTime")
    public ResponseEntity<?> getOrderByTime(@Valid @RequestBody CheckDayRequest checkDayRequest) {
        return ResponseEntity.ok(new BookedAtThisDayResponse(scheduleRepository.checkDayByTime(checkDayRequest.getDate())));
    }
}
