package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.payload.request.BookingOrderRequest;
import ru.nsu.carwash_server.payload.request.NewOrderRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.OrdersRepository;

import javax.validation.Valid;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    OrdersRepository ordersRepository;

    @PostMapping("/newOrder")
    public ResponseEntity<?> createOrder(@Valid @RequestBody NewOrderRequest newOrderRequest) {
        Order newOrder = new Order(newOrderRequest.getName(), newOrderRequest.getPrice(), newOrderRequest.getDate());
        ordersRepository.save(newOrder);
        return ResponseEntity.ok(new MessageResponse("New order added successfully!"));
    }

    @PostMapping("/bookOrder")
    public ResponseEntity<?> bookOrder(@Valid @RequestBody BookingOrderRequest bookingOrderRequest) {
        ordersRepository.changeOrderToBooked(true, bookingOrderRequest.getUserId(),
                bookingOrderRequest.getPrice(), bookingOrderRequest.getAutoId(),
                bookingOrderRequest.getSpecialist(), bookingOrderRequest.getAdministrator(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getOrderId(), bookingOrderRequest.getBonuses());
        return ResponseEntity.ok("Пользователь " + bookingOrderRequest.getUserId().toString()
                + " забронировал заказ " + bookingOrderRequest.getOrderId() + " с машиной: " + bookingOrderRequest.getAutoId());
    }
}
