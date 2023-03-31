package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.request.BookingOrderRequest;
import ru.nsu.carwash_server.payload.request.NewOrderRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.OrderInfoResponse;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

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
        return ResponseEntity.ok(new MessageResponse("Добавлен новый заказ с id: " + newOrder.getId()));
    }

    @PostMapping("/bookOrder")
    public ResponseEntity<?> newUserOrder(@Valid @RequestBody BookingOrderRequest bookingOrderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = new User(userDetails.getId());
        Order newOrder = new Order(bookingOrderRequest.getName(), bookingOrderRequest.getPrice(), bookingOrderRequest.getDate(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(), bookingOrderRequest.getBoxNumber(),
                bookingOrderRequest.getBonuses(), true, false,
                bookingOrderRequest.getComments(),new Auto(bookingOrderRequest.getAutoId()), user);
        ordersRepository.save(newOrder);
        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), newOrder.getPrice(), newOrder.getName(),
                newOrder.getDate(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), newOrder.getBonuses(), newOrder.isBooked(),
                newOrder.isExecuted(), newOrder.getComments(), newOrder.getUser().getId()));
    }

    @PostMapping("/updateOrderInfo")
    public ResponseEntity<?> updateOrderInfo(@Valid @RequestBody BookingOrderRequest bookingOrderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        ordersRepository.updateOrderInfo(true, userId,
                bookingOrderRequest.getPrice(), bookingOrderRequest.getAutoId(),
                bookingOrderRequest.getSpecialist(), bookingOrderRequest.getAdministrator(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getOrderId(), bookingOrderRequest.getBonuses(),
                bookingOrderRequest.getComments(), bookingOrderRequest.isExecuted());
        return ResponseEntity.ok(new MessageResponse("Пользователь " + userId
                + " забронировал заказ " + bookingOrderRequest.getOrderId() + " с машиной: "
                + bookingOrderRequest.getAutoId()));
    }
}
