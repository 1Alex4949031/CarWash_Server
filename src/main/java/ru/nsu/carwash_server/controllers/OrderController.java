package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import ru.nsu.carwash_server.payload.request.BookingTireOrderRequest;
import ru.nsu.carwash_server.payload.request.BookingWashingOrderRequest;
import ru.nsu.carwash_server.payload.request.GetBookedOrdersInTimeIntervalRequest;
import ru.nsu.carwash_server.payload.request.OrdersArrayPriceTimeRequest;
import ru.nsu.carwash_server.payload.request.UpdateOrderInfoRequest;
import ru.nsu.carwash_server.payload.response.ConnectedOrdersResponse;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.OrderInfoResponse;
import ru.nsu.carwash_server.payload.response.OrdersArrayResponse;
import ru.nsu.carwash_server.payload.response.TimeAndPriceResponse;
import ru.nsu.carwash_server.repository.OrdersPolishingRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.OrdersTireRepository;
import ru.nsu.carwash_server.repository.OrdersWashingRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

    @PutMapping("/updateOrderInfo")
    public ResponseEntity<?> updateOrderInfo(@Valid @RequestBody UpdateOrderInfoRequest updateOrderInfoRequest) {
        var user = userRepository.findByUsername(updateOrderInfoRequest.getUserPhone())
                                .orElseThrow(() -> new NotInDataBaseException("пользователей не найден пользователь с айди: ", updateOrderInfoRequest.getUserPhone().toString()));

        ordersRepository.updateOrderInfo(user.getId(), updateOrderInfoRequest.getPrice(),
                updateOrderInfoRequest.getAutoNumber(), updateOrderInfoRequest.getSpecialist(),
                updateOrderInfoRequest.getAdministrator(), updateOrderInfoRequest.getBoxNumber(),
                updateOrderInfoRequest.getOrderId(), updateOrderInfoRequest.getBonuses(),
                updateOrderInfoRequest.getComments(), updateOrderInfoRequest.isExecuted(),
                updateOrderInfoRequest.getStartTime(), updateOrderInfoRequest.getEndTime(),
                updateOrderInfoRequest.getOrderType());
        return ResponseEntity.ok(new MessageResponse("Информация изменена"));
    }

    @GetMapping("/getActualOrders")
    public ResponseEntity<?> getActualOrders(@RequestParam(name = "orderName", required = true) String orderName) {
        var includedOrders = ordersWashingRepository.findAllIncluded(orderName)
                .orElse(null);
        var connectedOrders = ordersWashingRepository.findAllConnected(orderName)
                .orElse(null);
        return ResponseEntity.ok(new ConnectedOrdersResponse(includedOrders, connectedOrders));
    }

    @PostMapping("/getBookedTimeInOneDay")
    public ResponseEntity<?> getBookedTimeInOneDay(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest
                                                           bookedOrdersInTimeIntervalRequest) {
        List<Order> orders = ordersRepository
                .getBookedOrdersInOneDayFull(bookedOrdersInTimeIntervalRequest.getStartTime(),
                        bookedOrdersInTimeIntervalRequest.getEndTime());
        List<SingleOrderResponse> ordersForResponse = new ArrayList<>();
        for (var item : orders) {
            SingleOrderResponse newItem;
            switch (item.getOrderType()) {
                case "polishing" -> {
                    List<String> stringOrders = new ArrayList<>();
                    for (var currentOrder : item.getOrdersPolishings()) {
                        stringOrders.add(currentOrder.getName().replace("_", " "));
                    }
                    newItem = new SingleOrderResponse(item.getId(), item.getStartTime(), item.getEndTime(),
                            item.getAdministrator(), item.getSpecialist(), item.getAutoNumber(), item.getAutoType(),
                            item.getBoxNumber(), item.getBonuses(), item.getPrice(), item.getWheelR(), item.isExecuted(),
                            item.getComments(), stringOrders, item.getUser().getPhone(), item.getOrderType());
                    break;
                }
                case "wash" -> {
                    List<String> stringOrders = new ArrayList<>();
                    for (var currentOrder : item.getOrdersWashing()) {
                        stringOrders.add(currentOrder.getName().replace("_", " "));
                    }

                    newItem = new SingleOrderResponse(item.getId(), item.getStartTime(), item.getEndTime(),
                            item.getAdministrator(), item.getSpecialist(), item.getAutoNumber(), item.getAutoType(),
                            item.getBoxNumber(), item.getBonuses(), item.getPrice(), item.getWheelR(), item.isExecuted(),
                            item.getComments(), stringOrders, item.getUser().getPhone(), item.getOrderType());
                    break;
                }
                case "tire" -> {
                    List<String> stringOrders = new ArrayList<>();
                    for (var currentOrder : item.getOrdersTires()) {
                        stringOrders.add(currentOrder.getName().replace("_", " "));
                    }
                    newItem = new SingleOrderResponse(item.getId(), item.getStartTime(), item.getEndTime(),
                            item.getAdministrator(), item.getSpecialist(), item.getAutoNumber(), item.getAutoType(),
                            item.getBoxNumber(), item.getBonuses(), item.getPrice(), item.getWheelR(), item.isExecuted(),
                            item.getComments(), stringOrders, item.getUser().getPhone(), item.getOrderType());
                    break;
                }
                default -> newItem = null;
            }
            ordersForResponse.add(newItem);
        }
        return ResponseEntity.ok(new OrdersArrayResponse(ordersForResponse));
    }

    @PostMapping("/bookWashingOrder")
    public ResponseEntity<?> newWashingOrder(@Valid @RequestBody BookingWashingOrderRequest bookingOrderRequest) {
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

    @PostMapping("/bookPolishingOrder")
    public ResponseEntity<?> newPolishingOrder(@Valid @RequestBody BookingWashingOrderRequest bookingOrderRequest) {
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

    @PostMapping("/bookTireOrder")
    public ResponseEntity<?> newTireOrder(@Valid @RequestBody BookingTireOrderRequest bookingOrderRequest) {
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
