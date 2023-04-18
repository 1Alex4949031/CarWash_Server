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
import ru.nsu.carwash_server.payload.request.GetBookedOrdersInOneDayRequest;
import ru.nsu.carwash_server.payload.request.NewOrderRequest;
import ru.nsu.carwash_server.payload.request.UpdateOrderInfoRequest;
import ru.nsu.carwash_server.payload.response.GetBookedOrdersInOneDayResponse;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.OrderInfoResponse;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.Set;


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
        var startTime = bookingOrderRequest.getStartTime();
        var boxNumber = bookingOrderRequest.getBoxNumber();
        Order newOrder = new Order(bookingOrderRequest.getName(), bookingOrderRequest.getPrice(),
                startTime, bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(), boxNumber,
                bookingOrderRequest.getBonuses(), true, false,
                bookingOrderRequest.getComments(), new Auto(bookingOrderRequest.getAutoId()), user);
        ordersRepository.save(newOrder);
        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), newOrder.getPrice(), newOrder.getName(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), newOrder.getBonuses(), newOrder.isBooked(),
                newOrder.isExecuted(), newOrder.getComments(), newOrder.getUser().getId()));
    }

    @PostMapping("/updateOrderInfo")
    public ResponseEntity<?> updateOrderInfo(@Valid @RequestBody UpdateOrderInfoRequest UpdateOrderInfoRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        ordersRepository.updateOrderInfo(true, userId,
                UpdateOrderInfoRequest.getPrice(), UpdateOrderInfoRequest.getAutoId(),
                UpdateOrderInfoRequest.getSpecialist(), UpdateOrderInfoRequest.getAdministrator(),
                UpdateOrderInfoRequest.getBoxNumber(), UpdateOrderInfoRequest.getOrderId(),
                UpdateOrderInfoRequest.getBonuses(), UpdateOrderInfoRequest.getComments(),
                UpdateOrderInfoRequest.isExecuted(), UpdateOrderInfoRequest.getStartTime(),
                UpdateOrderInfoRequest.getEndTime());
        return ResponseEntity.ok(UpdateOrderInfoRequest);
    }

    @PostMapping("/getBookedTimeInOneDay")
    public ResponseEntity<?> getBookedTimeInOneDay(@Valid @RequestBody GetBookedOrdersInOneDayRequest getBookedOrdersInOneDayRequest) {
        Set<Order> order = ordersRepository
                .getBookedOrdersInOneDay(getBookedOrdersInOneDayRequest.getStartTime(),
                        getBookedOrdersInOneDayRequest.getEndTime());
        return ResponseEntity.ok(new GetBookedOrdersInOneDayResponse(order));
    }
}
