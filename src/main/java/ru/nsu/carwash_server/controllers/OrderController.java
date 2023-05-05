package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.models.OrdersAdditional;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.EOrderAdditional;
import ru.nsu.carwash_server.payload.request.BookingOrderRequest;
import ru.nsu.carwash_server.payload.request.GetBookedOrdersInTimeIntervalRequest;
import ru.nsu.carwash_server.payload.response.GetBookedOrdersInTimeIntervalResponse;
import ru.nsu.carwash_server.payload.response.OrderInfoResponse;
import ru.nsu.carwash_server.repository.ExtraOrdersRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrdersRepository ordersRepository;

    private final ExtraOrdersRepository extraOrdersRepository;

    @Autowired
    public OrderController(
            OrdersRepository ordersRepository,
            ExtraOrdersRepository extraOrdersRepository) {
        this.ordersRepository = ordersRepository;
        this.extraOrdersRepository = extraOrdersRepository;
    }

    @PostMapping("/getBookedTimeInTimeInterval")
    public ResponseEntity<?> getBookedTimeInTimeInterval(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest bookedOrdersInTimeIntervalRequest) {
        Set<Order> order = ordersRepository
                .getBookedOrdersInTimeInterval(bookedOrdersInTimeIntervalRequest.getStartTime(),
                        bookedOrdersInTimeIntervalRequest.getEndTime());
        return ResponseEntity.ok(new GetBookedOrdersInTimeIntervalResponse(order));
    }

    @PostMapping("/getBookedTimeInOneDay")
    public ResponseEntity<?> getBookedTimeInOneDay(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest bookedOrdersInTimeIntervalRequest) {
        Set<Order> order = ordersRepository
                .getBookedOrdersInOneDay(bookedOrdersInTimeIntervalRequest.getStartTime(),
                        bookedOrdersInTimeIntervalRequest.getEndTime());
        return ResponseEntity.ok(new GetBookedOrdersInTimeIntervalResponse(order));
    }



    @PostMapping("/bookOrder")
    public ResponseEntity<?> newUserOrder(@Valid @RequestBody BookingOrderRequest bookingOrderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = new User(userDetails.getId());
        List<String> strExtraOrders = bookingOrderRequest.getExtraOrders();
        List<OrdersAdditional> ordersAdditional = new ArrayList<>();
        //Переводим дополнительные заказы из стр в енум
        if (strExtraOrders != null && !strExtraOrders.isEmpty()) {
            Set<EOrderAdditional> extraOrdersList = EnumSet.allOf(EOrderAdditional.class);
            ordersAdditional = strExtraOrders.stream().map(order -> {
                Optional<EOrderAdditional> eOrderAdditional = extraOrdersList.stream()
                        .filter(r -> r.name().equalsIgnoreCase(order))
                        .findAny();
                if (eOrderAdditional.isPresent()) {
                    return extraOrdersRepository.findByName(eOrderAdditional.get())
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая дополнительная услуга"));
                } else {
                    throw new RuntimeException("Error:Не существующая дополнительная услуга");
                }
            }).collect(Collectors.toList());
        }

        var startTime = bookingOrderRequest.getStartTime();
        var boxNumber = bookingOrderRequest.getBoxNumber();
        Order newOrder = new Order(ordersAdditional, startTime,
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(), boxNumber,
                bookingOrderRequest.getBonuses(), true, false,
                bookingOrderRequest.getComments(), bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), user);

        ordersRepository.save(newOrder);
        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), ordersAdditional,
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), newOrder.getBonuses(), newOrder.isBooked(),
                newOrder.isExecuted(), newOrder.getComments(),
                newOrder.getUser().getId(), newOrder.getPrice()));
    }

    /*@PostMapping("/newOrder")
    public ResponseEntity<?> createOrder(@Valid @RequestBody NewOrderRequest newOrderRequest) {
        Order newOrder = new Order(newOrderRequest.getName(), newOrderRequest.getPrice(), newOrderRequest.getDate());
        ordersRepository.save(newOrder);
        return ResponseEntity.ok(new MessageResponse("Добавлен новый заказ с id: " + newOrder.getId()));
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
    }*/
}
