package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.models.OrdersPolishing;
import ru.nsu.carwash_server.models.OrdersTire;
import ru.nsu.carwash_server.models.OrdersWashing;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.exception.NotInDataBaseException;
import ru.nsu.carwash_server.models.helpers.SingleOrderResponse;
import ru.nsu.carwash_server.models.helpers.TimeIntervals;
import ru.nsu.carwash_server.payload.request.BookingTireOrderRequest;
import ru.nsu.carwash_server.payload.request.BookingWashingPolishingOrderRequest;
import ru.nsu.carwash_server.payload.request.CreatingPolishingOrder;
import ru.nsu.carwash_server.payload.request.CreatingTireOrderRequest;
import ru.nsu.carwash_server.payload.request.CreatingWashingOrder;
import ru.nsu.carwash_server.payload.request.GetBookedOrdersInTimeIntervalRequest;
import ru.nsu.carwash_server.payload.request.OrdersArrayPriceAndGoodTimeRequest;
import ru.nsu.carwash_server.payload.request.OrdersArrayPriceTimeRequest;
import ru.nsu.carwash_server.payload.request.UpdateOrderInfoRequest;
import ru.nsu.carwash_server.payload.response.ActualOrdersResponse;
import ru.nsu.carwash_server.payload.response.ConnectedOrdersResponse;
import ru.nsu.carwash_server.payload.response.MainAndAdditionalResponse;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.OrderInfoResponse;
import ru.nsu.carwash_server.payload.response.OrdersArrayResponse;
import ru.nsu.carwash_server.payload.response.TimeAndPriceAndFreeTimeResponse;
import ru.nsu.carwash_server.payload.response.TimeAndPriceResponse;
import ru.nsu.carwash_server.repository.OrdersPolishingRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.OrdersTireRepository;
import ru.nsu.carwash_server.repository.OrdersWashingRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@ControllerAdvice
@RequestMapping("/api/orders")
public class OrderController {

    private final OrdersRepository ordersRepository;

    private final OrdersWashingRepository ordersWashingRepository;

    private final OrdersPolishingRepository ordersPolishingRepository;

    private final OrdersTireRepository ordersTireRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(
            OrdersRepository ordersRepository,
            OrdersWashingRepository ordersWashingRepository,
            OrdersTireRepository ordersTireRepository,
            OrdersPolishingRepository ordersPolishingRepository,
            UserRepository userRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersWashingRepository = ordersWashingRepository;
        this.ordersPolishingRepository = ordersPolishingRepository;
        this.ordersTireRepository = ordersTireRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/getServiceInfo")
    public ResponseEntity<?> getServiceInfo(@RequestParam(name = "orderName", required = true) String orderName,
                                            @RequestParam(name = "orderType", required = true) String orderType) {

        switch (orderType) {
            case "Wash" -> {
                var order = ordersWashingRepository.findByName(orderName)
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", orderName));
                return ResponseEntity.ok(order);
            }
            case "Polishing" -> {
                var order = ordersPolishingRepository.findByName(orderName)
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ",orderName));
                return ResponseEntity.ok(order);
            }
            case "Tire" -> {
                var order = ordersTireRepository.findByName(orderName)
                        .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", orderName));
                return ResponseEntity.ok(order);
            }
        }
        return ResponseEntity.ok( new MessageResponse("Такая услуга не найдена в базе данных"));
    }

    @PutMapping("/updateOrderInfo")
    public ResponseEntity<?> updateOrderInfo(@Valid @RequestBody UpdateOrderInfoRequest updateOrderInfoRequest) {
        if (updateOrderInfoRequest.getStartTime() != null && updateOrderInfoRequest.getEndTime() != null) {
            if (!checkTime(updateOrderInfoRequest.getStartTime(), updateOrderInfoRequest.getEndTime(), updateOrderInfoRequest.getBoxNumber())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Это время в этом боксе уже занято"));
            }
        }
        var user = userRepository.findByUsername(updateOrderInfoRequest.getUserPhone());
        String userContacts;
        if (user.isPresent()) {
            ordersRepository.updateOrderInfo(user.get().getId(), updateOrderInfoRequest.getPrice(),
                    updateOrderInfoRequest.getAutoNumber(), updateOrderInfoRequest.getSpecialist(),
                    updateOrderInfoRequest.getWheelR(),
                    updateOrderInfoRequest.getAdministrator(), updateOrderInfoRequest.getBoxNumber(),
                    updateOrderInfoRequest.getOrderId(), updateOrderInfoRequest.getBonuses(),
                    updateOrderInfoRequest.getComments(), updateOrderInfoRequest.isExecuted(),
                    updateOrderInfoRequest.getStartTime(), updateOrderInfoRequest.getEndTime(),
                    updateOrderInfoRequest.getOrderType(), user.get().getPhone());
        } else {
            userContacts = updateOrderInfoRequest.getUserPhone();
            ordersRepository.updateOrderInfo(null, updateOrderInfoRequest.getPrice(),
                    updateOrderInfoRequest.getAutoNumber(), updateOrderInfoRequest.getSpecialist(),
                    updateOrderInfoRequest.getWheelR(),
                    updateOrderInfoRequest.getAdministrator(), updateOrderInfoRequest.getBoxNumber(),
                    updateOrderInfoRequest.getOrderId(), updateOrderInfoRequest.getBonuses(),
                    updateOrderInfoRequest.getComments(), updateOrderInfoRequest.isExecuted(),
                    updateOrderInfoRequest.getStartTime(), updateOrderInfoRequest.getEndTime(),
                    updateOrderInfoRequest.getOrderType(), userContacts);
        }
        return ResponseEntity.ok(new MessageResponse("Информация изменена"));
    }

    @GetMapping("/getOrderInfo")
    public ResponseEntity<?> getOrderInfo(@RequestParam(name = "orderId", required = false) Long orderId) {
        System.out.println(orderId);
        if (orderId == null) {
            return ResponseEntity.ok(new MessageResponse("Передан null в айди"));
        }
        var orderById = ordersRepository.getById(orderId);
        List<String> ordersNames = new ArrayList<>();
        for (var washOrder : orderById.getOrdersWashing()) {
            ordersNames.add(washOrder.getName());
        }
        for (var polishingOrders : orderById.getOrdersPolishings()) {
            ordersNames.add(polishingOrders.getName());
        }
        for (var tireOrders : orderById.getOrdersTires()) {
            ordersNames.add(tireOrders.getName());
        }
        String userPhone = "Нет информации";
        if (orderById.getUserContacts() != null) {
            userPhone = orderById.getUserContacts();
        } else {
            userPhone = orderById.getUser().getPhone();
        }
        return ResponseEntity.ok(new SingleOrderResponse(orderId, orderById.getStartTime(),
                orderById.getEndTime(), orderById.getAdministrator(), orderById.getSpecialist(),
                orderById.getAutoNumber(), orderById.getAutoType(), orderById.getBoxNumber(), orderById.getBonuses(),
                orderById.getPrice(), orderById.getWheelR(), orderById.isExecuted(),
                orderById.getComments(), ordersNames, userPhone, orderById.getOrderType()));
    }

    @GetMapping("/getActualWashingOrders")
    public ResponseEntity<?> getActualWashingOrders(@RequestParam(name = "orderName", required = true) String orderName) {
        List<String> connectedOrders = switch (orderName) {
            case "Мойка_комплекс_(кузов_2_фазы_+_салон)" -> ordersWashingRepository.findAllAssociated("ELITE")
                    .orElse(null);
            case "Мойка_комплекс_(кузов_1_фаза_+_салон)" -> ordersWashingRepository.findAllAssociated("VIP")
                    .orElse(null);
            case "Мойка_кузова_2_фазы_без_протирки" -> ordersWashingRepository.findAllAssociated("Эконом")
                    .orElse(null);
            case "Мойка_кузова_2_фазы_с_протиркой" -> ordersWashingRepository.findAllAssociated("Стандарт")
                    .orElse(null);
            default -> new ArrayList<>();
        };
        List<String> includedOrders = new ArrayList<>();
        includedOrders.add(orderName);
        return ResponseEntity.ok(new ConnectedOrdersResponse(includedOrders, connectedOrders));
    }

    @GetMapping("/getAllWashingOrders")
    public ResponseEntity<?> getAllWashingOrders() {
        var mainOrders = ordersWashingRepository.findAllByRole("main")
                .orElse(null);
        var additionalOrders = ordersWashingRepository.findAllByRole("additional")
                .orElse(null);

        return ResponseEntity.ok(new MainAndAdditionalResponse(mainOrders, additionalOrders));
    }

    @GetMapping("/getActualPolishingOrders")
    public ResponseEntity<?> getActualPolishingOrders() {
        var orders = ordersPolishingRepository.getActualOrders()
                .orElse(null);
        return ResponseEntity.ok(new ActualOrdersResponse(orders));
    }

    @GetMapping("/getActualTireOrders")
    public ResponseEntity<?> getActualTireOrders() {
        var orders = ordersTireRepository.getActualOrders()
                .orElse(null);
        return ResponseEntity.ok(new ActualOrdersResponse(orders));
    }


    @PostMapping("/getBookedTimeInOneDay")
    public ResponseEntity<?> getBookedTimeInOneDay(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest
                                                           bookedOrdersInTimeIntervalRequest) {
        List<Order> orders = ordersRepository
                .getBookedOrdersInOneDayFull(bookedOrdersInTimeIntervalRequest.getStartTime(),
                        bookedOrdersInTimeIntervalRequest.getEndTime());
        List<SingleOrderResponse> ordersForResponse = getTimeAndPriceOfOrders(orders);
        return ResponseEntity.ok(new OrdersArrayResponse(ordersForResponse));
    }

    @PostMapping("/getPriceAndTimeForSite")
    public ResponseEntity<?> getPriceAndTimeForSite(@Valid @RequestBody OrdersArrayPriceAndGoodTimeRequest ordersArrayPriceTimeRequest) {
        List<TimeIntervals> timeIntervals = new ArrayList<>();
        int time = 0;
        int price = 0;
        switch (ordersArrayPriceTimeRequest.getOrderType()) {
            case "wash" -> {
                price = washingOrderPrice(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
                time = washingOrderTime(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
            }
            case "tire" ->
                    price = tireOrderPrice(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getWheelR());
            case "polishing" -> {
                price = polishingOrderPrice(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
                time = polishingOrderTime(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
            }
        }

        Date startTimeFromRequest = ordersArrayPriceTimeRequest.getStartTime();
        if (time < 75) {
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 1, 22, 8, ordersArrayPriceTimeRequest.getOrderType()));
        } else if (time <= 180) {
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 2, 20, 8, ordersArrayPriceTimeRequest.getOrderType()));
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 2, 19, 9, ordersArrayPriceTimeRequest.getOrderType()));
        } else {
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 6, 16, 8, ordersArrayPriceTimeRequest.getOrderType()));
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 6, 15, 9, ordersArrayPriceTimeRequest.getOrderType()));
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 6, 14, 10, ordersArrayPriceTimeRequest.getOrderType()));
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 6, 13, 11, ordersArrayPriceTimeRequest.getOrderType()));
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 6, 13, 12, ordersArrayPriceTimeRequest.getOrderType()));
            timeIntervals.addAll(fillTimeIntervals(startTimeFromRequest, 6, 14, 13, ordersArrayPriceTimeRequest.getOrderType()));
        }
        List<Order> orders = ordersRepository
                .getBookedOrdersInOneDayFull(ordersArrayPriceTimeRequest.getStartTime(),
                        ordersArrayPriceTimeRequest.getEndTime());

        List<SingleOrderResponse> bookedOrders = getTimeAndPriceOfOrders(orders);

        List<TimeIntervals> clearOrders = new ArrayList<>(timeIntervals);

        for (var bookedOrder : bookedOrders) {
            for (var freeTime : timeIntervals) {
                Date startTimeBooked = bookedOrder.getStartTime();
                Date endTimeBooked = bookedOrder.getEndTime();
                Date startTimeFree = freeTime.getStartTime();
                Date endTimeFree = freeTime.getEndTime();
                int freeBox = freeTime.getBox();
                int bookedBox = bookedOrder.getBoxNumber();
                if (((startTimeFree.compareTo(startTimeBooked) >= 0 && endTimeFree.compareTo(endTimeBooked) <= 0)
                        || (startTimeFree.compareTo(startTimeBooked) <= 0 && endTimeFree.compareTo(startTimeBooked) > 0)
                        || (startTimeFree.compareTo(endTimeBooked) < 0 && endTimeBooked.compareTo(endTimeFree) <= 0))
                        && freeBox == bookedBox) {
                    TimeIntervals timeIntervals1 = new TimeIntervals(startTimeFree, endTimeFree, freeBox);
                    clearOrders.remove(timeIntervals1);
                }
            }
        }

        List<TimeIntervals> noDuplicatesTimeList = new ArrayList<>(clearOrders);

        for (var firstInterval : clearOrders) {
            for (var secondInterval : clearOrders) {
                if (noDuplicatesTimeList.contains(secondInterval) && (firstInterval.getStartTime().equals(secondInterval.getStartTime())
                        && firstInterval.getEndTime().equals(secondInterval.getEndTime())
                        && !Objects.equals(firstInterval.getBox(), secondInterval.getBox()))
                        && noDuplicatesTimeList.contains(secondInterval) && noDuplicatesTimeList.contains(firstInterval)) {
                    noDuplicatesTimeList.remove(secondInterval);
                }
            }
        }

        return ResponseEntity.ok(new

                TimeAndPriceAndFreeTimeResponse(price, time, noDuplicatesTimeList));
    }

    @Transactional
    @PostMapping("/bookWashingOrder")
    public ResponseEntity<?> newWashingOrder(@Valid @RequestBody BookingWashingPolishingOrderRequest bookingOrderRequest) {
        if (!checkTime(bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(), bookingOrderRequest.getBoxNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Это время в этом боксе уже занято"));
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = new User(userDetails.getId());
        List<OrdersWashing> ordersWashings = new ArrayList<>();

        for (var order : bookingOrderRequest.getOrders()) {
            var currentOrder = ordersWashingRepository.findByName(order.replace(" ", "_"))
                    .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", order));
            ordersWashings.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = washingOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getAutoType());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersWashings, bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getBonuses(),
                false, bookingOrderRequest.getComments(),
                bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), user, "wash");
        newOrder.setPrice(price);
        ordersRepository.save(newOrder);

        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), bookingOrderRequest.getOrders(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), newOrder.getBonuses(),
                newOrder.isExecuted(), newOrder.getComments(),
                newOrder.getUser().getId(), newOrder.getPrice(), "wash", "wash"));
    }

    @Transactional
    @PostMapping("/bookPolishingOrder")
    public ResponseEntity<?> newPolishingOrder(@Valid @RequestBody BookingWashingPolishingOrderRequest bookingOrderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = new User(userDetails.getId());
        List<OrdersPolishing> ordersPolishings = new ArrayList<>();

        for (var order : bookingOrderRequest.getOrders()) {
            var currentOrder = ordersPolishingRepository.findByName(order.replace(" ", "_"))
                    .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", order));
            ordersPolishings.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = polishingOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getAutoType());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersPolishings, bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getBonuses(),
                false, bookingOrderRequest.getComments(),
                bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), user, "polishing", price);
        ordersRepository.save(newOrder);

        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), bookingOrderRequest.getOrders(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), newOrder.getAutoNumber(),
                newOrder.getAutoType(), newOrder.getBonuses(),
                newOrder.isExecuted(), newOrder.getComments(),
                newOrder.getUser().getId(), newOrder.getPrice(), newOrder.getOrderType(), "polishing"));
    }

    @Transactional
    @PostMapping("/bookTireOrder")
    public ResponseEntity<?> newTireOrder(@Valid @RequestBody BookingTireOrderRequest bookingOrderRequest) {
        if (!checkTime(bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(), bookingOrderRequest.getBoxNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Это время в этом боксе уже занято"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = new User(userDetails.getId());
        List<OrdersTire> ordersTireService = new ArrayList<>();

        for (var order : bookingOrderRequest.getOrders()) {
            var currentOrder = ordersTireRepository.findByName(order.replace(" ", "_"))
                    .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", order));
            ordersTireService.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = tireOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getWheelR());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersTireService, bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getBonuses(),
                false, bookingOrderRequest.getComments(),
                bookingOrderRequest.getAutoNumber(), bookingOrderRequest.getAutoType(),
                user, "tire", price, bookingOrderRequest.getWheelR());
        ordersRepository.save(newOrder);

        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), bookingOrderRequest.getOrders(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), newOrder.getAutoNumber(),
                newOrder.getAutoType(), newOrder.getBonuses(),
                newOrder.isExecuted(), newOrder.getComments(),
                newOrder.getUser().getId(), newOrder.getPrice(), newOrder.getOrderType(), newOrder.getWheelR()));
    }

    @PostMapping("/getPriceAndTime")
    public ResponseEntity<?> getPriceAndTime(@Valid @RequestBody OrdersArrayPriceTimeRequest ordersArrayPriceTimeRequest) {
        int price = 0;
        int time = 0;
        switch (ordersArrayPriceTimeRequest.getOrderType()) {
            case "wash" -> {
                price = washingOrderPrice(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
                time = washingOrderTime(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
            }
            case "tire" ->
                    price = tireOrderPrice(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getWheelR());
            case "polishing" -> {
                price = polishingOrderPrice(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
                time = polishingOrderTime(ordersArrayPriceTimeRequest.getOrders(), ordersArrayPriceTimeRequest.getBodyType());
            }
        }
        return ResponseEntity.ok(new TimeAndPriceResponse(price, time));
    }


    @PostMapping("/createWashingOrder")
    @Transactional
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> creatingWashingOrder(@Valid @RequestBody CreatingWashingOrder bookingOrderRequest) {
        if (!checkTime(bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(), bookingOrderRequest.getBoxNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Это время в этом боксе уже занято"));
        }

        List<OrdersWashing> ordersWashings = new ArrayList<>();

        for (var order : bookingOrderRequest.getOrders()) {
            var currentOrder = ordersWashingRepository.findByName(order.replace(" ", "_"))
                    .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", order));
            ordersWashings.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = washingOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getAutoType());
        }

        Order newOrder = new Order(ordersWashings, bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getBonuses(),
                false, bookingOrderRequest.getComments(),
                bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), bookingOrderRequest.getUserContacts(), "wash");
        newOrder.setPrice(price);
        ordersRepository.save(newOrder);

        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), bookingOrderRequest.getOrders(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), newOrder.getBonuses(),
                newOrder.isExecuted(), newOrder.getComments(), newOrder.getPrice(), "wash", "wash", bookingOrderRequest.getUserContacts()));
    }


    @PostMapping("/createPolishingOrder")
    @Transactional
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> creatingPolishingOrder(@Valid @RequestBody CreatingPolishingOrder bookingOrderRequest) {
        if (!checkTime(bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(), bookingOrderRequest.getBoxNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Это время в этом боксе уже занято"));
        }

        List<OrdersPolishing> ordersPolishings = new ArrayList<>();

        for (var order : bookingOrderRequest.getOrders()) {
            var currentOrder = ordersPolishingRepository.findByName(order.replace(" ", "_"))
                    .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", order));
            ordersPolishings.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = washingOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getAutoType());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersPolishings, bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getBonuses(),
                false, bookingOrderRequest.getComments(),
                bookingOrderRequest.getAutoNumber(),
                bookingOrderRequest.getAutoType(), bookingOrderRequest.getUserContacts(), "polishing", price);

        newOrder.setPrice(price);
        ordersRepository.save(newOrder);

        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), bookingOrderRequest.getOrders(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), newOrder.getAutoNumber(),
                newOrder.getAutoType(), newOrder.getBonuses(),
                newOrder.isExecuted(), newOrder.getComments(),
                newOrder.getPrice(), newOrder.getOrderType(), "polishing", bookingOrderRequest.getUserContacts()));
    }

    @PostMapping("/createTireOrder")
    @Transactional
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createTireOrder(@Valid @RequestBody CreatingTireOrderRequest bookingOrderRequest) {
        if (!checkTime(bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(), bookingOrderRequest.getBoxNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Это время в этом боксе уже занято"));
        }

        List<OrdersTire> ordersTireService = new ArrayList<>();

        for (var order : bookingOrderRequest.getOrders()) {
            var currentOrder = ordersTireRepository.findByName(order.replace(" ", "_"))
                    .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", order));
            ordersTireService.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = tireOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getWheelR());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersTireService, bookingOrderRequest.getStartTime(), bookingOrderRequest.getEndTime(),
                bookingOrderRequest.getAdministrator(), bookingOrderRequest.getSpecialist(),
                bookingOrderRequest.getBoxNumber(), bookingOrderRequest.getBonuses(),
                false, bookingOrderRequest.getComments(),
                bookingOrderRequest.getAutoNumber(), bookingOrderRequest.getAutoType(),
                bookingOrderRequest.getUserContacts(), "tire", price, bookingOrderRequest.getWheelR());
        ordersRepository.save(newOrder);

        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), bookingOrderRequest.getOrders(),
                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
                newOrder.getBoxNumber(), newOrder.getAutoNumber(),
                newOrder.getAutoType(), newOrder.getBonuses(),
                newOrder.isExecuted(), newOrder.getComments(), newOrder.getPrice(),
                newOrder.getOrderType(), newOrder.getWheelR(), bookingOrderRequest.getUserContacts()));
    }

    public List<TimeIntervals> fillTimeIntervals(Date startTimeFromRequest, int timeSkip, int endOfFor, int startOfFor, String orderType) {
        List<TimeIntervals> timeIntervals = new ArrayList<>();
        for (int i = startOfFor; i < endOfFor; i += timeSkip) {
            LocalDateTime localDateStartTime = LocalDateTime.ofInstant(startTimeFromRequest.toInstant(),
                            ZoneId.systemDefault())
                    .withHour(i)
                    .withMinute(0)
                    .withSecond(0);
            Date startTime = Date.from(localDateStartTime.atZone(ZoneId.systemDefault()).toInstant());

            LocalDateTime localDateEndTime = LocalDateTime.ofInstant(startTimeFromRequest.toInstant(),
                            ZoneId.systemDefault())
                    .withHour(i + timeSkip)
                    .withMinute(0)
                    .withSecond(0);
            Date endTime = Date.from(localDateEndTime.atZone(ZoneId.systemDefault()).toInstant());

            switch (orderType) {
                case "wash" -> {
                    TimeIntervals singleTimeIntervalFirstBox = new TimeIntervals(startTime, endTime, 1);
                    timeIntervals.add(singleTimeIntervalFirstBox);
                    TimeIntervals singleTimeIntervalSecondBox = new TimeIntervals(startTime, endTime, 2);
                    timeIntervals.add(singleTimeIntervalSecondBox);
                    TimeIntervals singleTimeIntervalThirdBox = new TimeIntervals(startTime, endTime, 3);
                    timeIntervals.add(singleTimeIntervalThirdBox);
                }
                case "tire" -> {
                    TimeIntervals singleTimeIntervalFirstBox = new TimeIntervals(startTime, endTime, 0);
                    timeIntervals.add(singleTimeIntervalFirstBox);
                }
                case "polishing" -> {
                    TimeIntervals singleTimeIntervalFirstBox = new TimeIntervals(startTime, endTime, 5);
                    timeIntervals.add(singleTimeIntervalFirstBox);
                }
            }
        }
        return timeIntervals;
    }

    public boolean checkTime(Date startTime, Date endTime, int box) {
        List<Order> orders = ordersRepository
                .getBookedOrdersInOneDayFullInBox(startTime,
                        endTime, box);

        List<SingleOrderResponse> bookedOrders = getTimeAndPriceOfOrders(orders);

        for (var bookedOrder : bookedOrders) {
            Date startTimeBooked = bookedOrder.getStartTime();
            Date endTimeBooked = bookedOrder.getEndTime();
            int bookedBox = bookedOrder.getBoxNumber();
            if (((startTime.compareTo(startTimeBooked) >= 0 && endTime.compareTo(endTimeBooked) <= 0)
                    || (startTime.compareTo(startTimeBooked) <= 0 && endTime.compareTo(startTimeBooked) > 0)
                    || (startTime.compareTo(endTimeBooked) < 0 && endTimeBooked.compareTo(endTime) <= 0))
                    && box == bookedBox) {
                return false;
            }
        }
        return true;
    }

    public int washingOrderPrice(List<String> orderArray, int bodyType) {
        int price = 0;
        if (bodyType == 1) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", item));
                price += currentOrder.getPriceFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", item));
                price += currentOrder.getPriceSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", item));
                price += currentOrder.getPriceThirdType();
            }
        }
        return price;
    }

    public int washingOrderTime(List<String> orderArray, int bodyType) {
        int time = 15;
        if (bodyType == 1) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", item));
                time += currentOrder.getTimeFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", item));
                time += currentOrder.getTimeSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг мойки не найдена услуга: ", item));
                time += currentOrder.getTimeThirdType();
            }
        }
        return time;
    }

    public int polishingOrderPrice(List<String> orderArray, int bodyType) {
        int price = 0;
        if (bodyType == 1) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", item));
                price += currentOrder.getPriceFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", item));
                price += currentOrder.getPriceSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", item));
                price += currentOrder.getPriceThirdType();
            }
        }
        return price;
    }

    public int polishingOrderTime(List<String> orderArray, int bodyType) {
        int time = 15;
        if (bodyType == 1) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", item));
                time += currentOrder.getTimeFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", item));
                time += currentOrder.getTimeSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new NotInDataBaseException("услуг полировки не найдена услуга: ", item));
                time += currentOrder.getTimeThirdType();
            }
        }
        return time;
    }


    public List<SingleOrderResponse> getTimeAndPriceOfOrders(List<Order> orders) {
        List<SingleOrderResponse> ordersForResponse = new ArrayList<>();

        for (var item : orders) {
            SingleOrderResponse newItem;
            switch (item.getOrderType()) {
                case "polishing" -> {
                    List<String> stringOrders = new ArrayList<>();
                    for (var currentOrder : item.getOrdersPolishings()) {
                        stringOrders.add(currentOrder.getName().replace("_", " "));
                    }
                    String userContact;
                    if (item.getUser() == null) {
                        userContact = item.getUserContacts();
                    } else {
                        userContact = item.getUser().getPhone();
                    }

                    newItem = new SingleOrderResponse(item.getId(), item.getStartTime(), item.getEndTime(),
                            item.getAdministrator(), item.getSpecialist(), item.getAutoNumber(), item.getAutoType(),
                            item.getBoxNumber(), item.getBonuses(), item.getPrice(), item.getWheelR(), item.isExecuted(),
                            item.getComments(), stringOrders, userContact, item.getOrderType());
                    break;
                }
                case "wash" -> {
                    List<String> stringOrders = new ArrayList<>();
                    for (var currentOrder : item.getOrdersWashing()) {
                        stringOrders.add(currentOrder.getName().replace("_", " "));
                    }
                    String userContact;
                    if (item.getUser() == null) {
                        userContact = item.getUserContacts();
                    } else {
                        userContact = item.getUser().getPhone();
                    }
                    newItem = new SingleOrderResponse(item.getId(), item.getStartTime(), item.getEndTime(),
                            item.getAdministrator(), item.getSpecialist(), item.getAutoNumber(), item.getAutoType(),
                            item.getBoxNumber(), item.getBonuses(), item.getPrice(), item.getWheelR(), item.isExecuted(),
                            item.getComments(), stringOrders, userContact, item.getOrderType());
                    break;
                }
                case "tire" -> {
                    List<String> stringOrders = new ArrayList<>();
                    for (var currentOrder : item.getOrdersTires()) {
                        stringOrders.add(currentOrder.getName().replace("_", " "));
                    }
                    String userContact;
                    if (item.getUser() == null) {
                        userContact = item.getUserContacts();
                    } else {
                        userContact = item.getUser().getPhone();
                    }

                    newItem = new SingleOrderResponse(item.getId(), item.getStartTime(), item.getEndTime(),
                            item.getAdministrator(), item.getSpecialist(), item.getAutoNumber(), item.getAutoType(),
                            item.getBoxNumber(), item.getBonuses(), item.getPrice(), item.getWheelR(), item.isExecuted(),
                            item.getComments(), stringOrders, userContact, item.getOrderType());
                    break;
                }
                default -> newItem = null;
            }
            ordersForResponse.add(newItem);
        }
        return ordersForResponse;
    }


    public int tireOrderPrice(List<String> orderArray, String wheelR) {
        int price = 0;
        switch (wheelR) {
            case "R13":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_13();
                }
                break;
            case "R14":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_14();
                }
                break;
            case "R15":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_15();
                }
                break;
            case "R16":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_16();
                }
                break;
            case "R17":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_17();
                }
                break;
            case "R18":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_18();
                }
                break;
            case "R19":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_19();
                }
                break;
            case "R20":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_20();
                }
                break;
            case "R21":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_21();
                }
                break;
            case "R22":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new NotInDataBaseException("услуг шиномонтажа не найдена услуга: ", item));
                    price += currentOrder.getPrice_r_22();
                }
                break;
        }
        return price;
    }
}
