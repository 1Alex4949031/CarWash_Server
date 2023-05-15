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
import ru.nsu.carwash_server.models.OrdersPolishing;
import ru.nsu.carwash_server.models.OrdersTire;
import ru.nsu.carwash_server.models.OrdersWashing;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.helpers.SingleOrderResponse;
import ru.nsu.carwash_server.payload.request.BookingTireOrderRequest;
import ru.nsu.carwash_server.payload.request.BookingWashingOrderRequest;
import ru.nsu.carwash_server.payload.request.GetBookedOrdersInTimeIntervalRequest;
import ru.nsu.carwash_server.payload.request.OrdersArrayPriceTimeRequest;
import ru.nsu.carwash_server.payload.response.GetBookedOrdersInTimeIntervalResponse;
import ru.nsu.carwash_server.payload.response.OrderInfoResponse;
import ru.nsu.carwash_server.payload.response.OrdersArrayResponse;
import ru.nsu.carwash_server.payload.response.TimeAndPriceResponse;
import ru.nsu.carwash_server.repository.OrdersPolishingRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.OrdersTireRepository;
import ru.nsu.carwash_server.repository.OrdersWashingRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrdersRepository ordersRepository;

    private final OrdersWashingRepository ordersWashingRepository;

    private final OrdersPolishingRepository ordersPolishingRepository;

    private final OrdersTireRepository ordersTireRepository;

    @Autowired
    public OrderController(
            OrdersRepository ordersRepository,
            OrdersWashingRepository ordersWashingRepository,
            OrdersTireRepository ordersTireRepository,
            OrdersPolishingRepository ordersPolishingRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersWashingRepository = ordersWashingRepository;
        this.ordersPolishingRepository = ordersPolishingRepository;
        this.ordersTireRepository = ordersTireRepository;
    }

    @PostMapping("/getBookedTimeInTimeInterval")
    public ResponseEntity<?> getBookedTimeInTimeInterval(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest bookedOrdersInTimeIntervalRequest) {
        List<Order> order = ordersRepository
                .getBookedOrdersInTimeInterval(bookedOrdersInTimeIntervalRequest.getStartTime(),
                        bookedOrdersInTimeIntervalRequest.getEndTime());
        return ResponseEntity.ok(new GetBookedOrdersInTimeIntervalResponse(order));
    }

    @PostMapping("/getBookedTimeInOneDay2")
    public ResponseEntity<?> getBookedTimeInOneDay2(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest
                                                            bookedOrdersInTimeIntervalRequest) {
        List<Order> order = ordersRepository
                .getBookedOrdersInOneDay(bookedOrdersInTimeIntervalRequest.getStartTime(),
                        bookedOrdersInTimeIntervalRequest.getEndTime());
        return ResponseEntity.ok(new GetBookedOrdersInTimeIntervalResponse(order));
    }

    @PostMapping("/getBookedTimeInOneDay")
    public ResponseEntity<?> getBookedTimeInOneDay(@Valid @RequestBody GetBookedOrdersInTimeIntervalRequest
                                                           bookedOrdersInTimeIntervalRequest) {
        List<Order> orders = ordersRepository
                .getBookedOrdersInOneDay(bookedOrdersInTimeIntervalRequest.getStartTime(),
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
                    newItem = new SingleOrderResponse(item.getStartTime(), item.getEndTime(),
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

                    newItem = new SingleOrderResponse(item.getStartTime(), item.getEndTime(),
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
                    newItem = new SingleOrderResponse(item.getStartTime(), item.getEndTime(),
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
                    .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + order));
            ordersWashings.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = washingOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getAutoType());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersWashings, bookingOrderRequest.getStartTime(),
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
                    .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + order));
            ordersPolishings.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = polishingOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getAutoType());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersPolishings, bookingOrderRequest.getStartTime(),
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
                    .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + order));
            ordersTireService.add(currentOrder);
        }

        Integer price = bookingOrderRequest.getPrice();

        if (bookingOrderRequest.getPrice() == null || bookingOrderRequest.getPrice() == 0) {
            price = tireOrderPrice(bookingOrderRequest.getOrders(), bookingOrderRequest.getWheelR());
            //логика подсчёта цены
        }

        Order newOrder = new Order(ordersTireService, bookingOrderRequest.getStartTime(),
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
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                price += currentOrder.getPriceFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                price += currentOrder.getPriceSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
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
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                time += currentOrder.getTimeFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                time += currentOrder.getTimeSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersWashingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
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
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                price += currentOrder.getPriceFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                price += currentOrder.getPriceSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
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
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                time += currentOrder.getTimeFirstType();
            }
        } else if (bodyType == 2) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                time += currentOrder.getTimeSecondType();
            }

        } else if (bodyType == 3) {
            for (var item : orderArray) {
                var currentOrder = ordersPolishingRepository.findByName(item.replace(" ", "_"))
                        .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
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
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_13();
                }
                break;
            case "R14":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_14();
                }
                break;
            case "R15":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_15();
                }
                break;
            case "R16":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_16();
                }
                break;
            case "R17":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_17();
                }
                break;
            case "R18":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_18();
                }
                break;
            case "R19":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_19();
                }
                break;
            case "R20":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_20();
                }
                break;
            case "R21":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_21();
                }
                break;
            case "R22":
                for (var item : orderArray) {
                    var currentOrder = ordersTireRepository.findByName(item.replace(" ", "_"))
                            .orElseThrow(() -> new RuntimeException("Error: Не существующая услуга - " + item));
                    price += currentOrder.getPrice_r_22();
                }
                break;
        }
        return price;
    }
//    @PostMapping("/getPriceAndTime")
//    public ResponseEntity<?> getPriceAndTime(@Valid @RequestBody OrdersArrayRu ordersArrayRu) {
//        ArrayList<String> ordersNameEng = new ArrayList<>();
//        TimeAndPriceResponse response = new TimeAndPriceResponse();
//        response.setPrice(0);
//        response.setTime(0);
//        if (ordersArrayRu.getOrdersRu() == null || ordersArrayRu.getOrdersRu().isEmpty()) {
//            return ResponseEntity.ok(response);
//
//        }
//        for (var item : ordersArrayRu.getOrdersRu()) {
//            switch (item) {
//                case "Турбо сушка кузова" -> ordersNameEng.add("TURBO_DRYING");
//                case "Продувка кузова" -> ordersNameEng.add("BODY_BLOW");
//                case "Продувка замков, зеркала" -> ordersNameEng.add("LOCKS_AND_MIRRORS_BLOW");
//                case "Обработка силиконом" -> ordersNameEng.add("SILICONE_TREATMENT");
//                case "Обработка замков жидкостью" -> ordersNameEng.add("LOCK_FLUID_TREATMENT");
//                case "Обработка кожи кондиционером 1 эл." -> ordersNameEng.add("LEATHER_CONDITIONER_TREATMENT");
//                case "Полироль пластика салона" -> ordersNameEng.add("SALON_PLASTIC_POLISH");
//                case "Полироль пластика панель" -> ordersNameEng.add("PANEL_PLASTIC_POLISH");
//                case "Полироль пластика багажник" -> ordersNameEng.add("TRUNK_PLASTIC_POLISH");
//                case "Наружная мойка радиатора" -> ordersNameEng.add("RADIATOR_WASH");
//                case "Чернение шин 4" -> ordersNameEng.add("TIRE_BLACKENING");
//                case "Озонирование салона 30 мин." -> ordersNameEng.add("SALON_OZONATION");
//                case "Очистка битумных пятен кузов" -> ordersNameEng.add("CLEANING_BITUMEN_STAINS_BODY");
//                case "Покрытие лобового стекла Nano Glass 1 эл." -> ordersNameEng.add("WINDSHIELD_NANO_GLASS_COATING");
//                case "Покрытие бокового стекла Nano Glass 1 эл." -> ordersNameEng.add("SIDE_GLASS_NANO_GLASS_COATING");
//                case "Комплекс всех стёкол" -> ordersNameEng.add("COMPLEX_ALL_GLASSES");
//                case "Диэлектрическая химчистка двигателя" -> ordersNameEng.add("ENGINE_CHEMICAL_CLEANING");
//                case "Химчистка дисков 4 шт." -> ordersNameEng.add("DISC_CHEMICAL_CLEANING");
//                case "Химчистка багажника" -> ordersNameEng.add("TRUNK_CHEMICAL_CLEANING");
//                case "Химчистка двери 1 эл." -> ordersNameEng.add("OOR_CHEMICAL_CLEANING");
//                case "Химчистка кресло (текстиль) 1 эл." -> ordersNameEng.add("TEXTILE_SEAT_CHEMICAL_CLEANING");
//                case "Химчистка кресло (кожа) 1 эл." -> ordersNameEng.add("LEATHER_SEAT_CHEMICAL_CLEANING");
//                case "Химчистка передней панели" -> ordersNameEng.add("FRONT_PANEL_CHEMICAL_CLEANING");
//                case "Химчистка пола" -> ordersNameEng.add("FLOOR_CHEMICAL_CLEANING");
//                case "Химчистка потолка" -> ordersNameEng.add("CEILING_CHEMICAL_CLEANING");
//                case "Однофазная мойка с химией без протирки" ->
//                        ordersNameEng.add("ONE_PHASE_WASH_WITH_CHEMICALS_NO_WIPING");
//                case "Мойка кузова 2 фазы с протиркой" -> ordersNameEng.add("TWO_PHASE_WASH_WITH_WIPING");
//                case "Мойка кузова 2 фазы без протирки" -> ordersNameEng.add("TWO_PHASE_WASH_NO_WIPING");
//                case "Мойка комплекс (кузов 1 фазы + салон)" -> ordersNameEng.add("COMPLEX_WASH_1_PHASES_SALON");
//                case "Мойка комплекс (кузов 2 фазы + салон)" -> ordersNameEng.add("COMPLEX_WASH_2_PHASES_SALON");
//                case "Покрытие кварцевой защитой" -> ordersNameEng.add("QUARTZ_COATING");
//                case "Мойка двигателя с хим. раствором + сушка" ->
//                        ordersNameEng.add("ENGINE_WASH_WITH_CHEMICALS_AND_DRYING");
//                case "Очистка арок колес" -> ordersNameEng.add("WHEEL_ARCHES_CLEANING");
//                case "Уборка багажника" -> ordersNameEng.add("TRUNK_CLEANING");
//                case "Влажная уборка салона" -> ordersNameEng.add("INNER_WET_CLEANING");
//                case "Влажная уборка передней панели" -> ordersNameEng.add("FRONT_PANEL_WET_CLEANING");
//                case "Пылесос салона" -> ordersNameEng.add("VACUUM_CLEANING_SALON");
//                case "Пылесос пола" -> ordersNameEng.add("VACUUM_CLEANING_FLOOR");
//                case "Пылесос ковриков" -> ordersNameEng.add("VACUUM_CLEANING_CARPETS");
//                case "Коврик багажников" -> ordersNameEng.add("CARGO_MAT");
//                case "Резиновый коврик 1 шт." -> ordersNameEng.add("RUBBER_MAT_1");
//                case "Стирка текстильного коврика 1 шт." -> ordersNameEng.add("TEXTILE_MAT_WASH");
//                case "Чистка стёкол с 2х сторон" -> ordersNameEng.add("GLASS_CLEANING_2_SIDES");
//                case "Чистка стёкол внутри салона" -> ordersNameEng.add("INNER_GLASS_CLEANING");
//                case "Чистка ветрового стекла" -> ordersNameEng.add("WINDSHIELD_CLEANING");
//                case "Полировка восстановительная" -> ordersNameEng.add("POLISHING_RESTORATION");
//                case "Глубокая абразивная полировка" -> ordersNameEng.add("DEEP_ABRASIVE_POLISHING");
//                case "Полировка фар 1 шт" -> ordersNameEng.add("HEADLINE_POLISHING");
//                case "Полимер Sonax до 6 месяцев" -> ordersNameEng.add("PROFESSIONAL_SONIX_POLYMER_COATING");
//                case "Кварцекерамическое покрытие CAN COAT до 6 месяцев" ->
//                        ordersNameEng.add("PROFESSIONAL_QUARTZ_CERAMIC_COATING");
//                case "Koch Chemie 1K-NANO 1 год" -> ordersNameEng.add("PROFESSIONAL_KOCH_CHEMIE_1K_NANO");
//                case "Профессиональное покрытие керамика (2 слоя + 1 слой) до 3 лет" ->
//                        ordersNameEng.add("PROFESSIONAL_CERAMIC_COATING_2_LAYERS_ONE_LAYER");
//                default -> throw new RuntimeException("No such Enum constant" + item);
//            }
//        }
//        int price = 0;
//        int time = 15;
//        int carBodyType = ordersArrayRu.getBodyType();
//        List<EOrderAdditional> orderEnums = new ArrayList<>();
//
//        for (int i = 0; i < ordersNameEng.size(); i++) {
//            orderEnums.add(EOrderAdditional.valueOf(ordersNameEng.get(i)));
//            price += orderEnums.get(i).getOrderInfo().getPriceForBodyType(carBodyType);
//            time += orderEnums.get(i).getOrderInfo().getTimeForBodyType(carBodyType);
//        }
//        response.setPrice(price);
//        response.setTime(time);
//        return ResponseEntity.ok(response);
//    }


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
