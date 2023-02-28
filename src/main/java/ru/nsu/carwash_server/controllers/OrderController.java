package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Orders;
import ru.nsu.carwash_server.payload.request.BookingOrderRequest;
import ru.nsu.carwash_server.payload.request.NewOrderRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.OrdersRepository;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    OrdersRepository ordersRepository;

    @PostMapping("/newOrder")
    public ResponseEntity<?> createOrder(@Valid @RequestBody NewOrderRequest newOrderRequest) {
        Orders newOrder = new Orders(newOrderRequest.getName(), newOrderRequest.getPrice(), newOrderRequest.getDate());
        ordersRepository.save(newOrder);
        return ResponseEntity.ok(new MessageResponse("New order added successfully!"));
    }

    /*@PostMapping("/bookOrder")
    public ResponseEntity<?> bookOrder(@Valid @RequestBody BookingOrderRequest bookingOrderRequest) {
        ordersRepository.changeOrderToBooked(true, bookingOrderRequest.getUser().getId(),
                bookingOrderRequest.getOrder().getId());
        return ResponseEntity.ok(new MessageResponse("User" + bookingOrderRequest.getUser().getUsername()
                + "забронировал заказ" + bookingOrderRequest.getOrder().getId()));
    }*/
}
