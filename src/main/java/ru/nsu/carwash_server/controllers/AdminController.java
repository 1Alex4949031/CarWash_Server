package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.response.UserCarsResponse;
import ru.nsu.carwash_server.payload.response.UserInformationResponse;
import ru.nsu.carwash_server.payload.response.UserOrdersResponse;
import ru.nsu.carwash_server.repository.ExtraOrdersRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.jwt.JwtUtils;
import ru.nsu.carwash_server.security.services.RefreshTokenService;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final OrdersRepository ordersRepository;

    private final ExtraOrdersRepository extraOrdersRepository;

    @Autowired
    public AdminController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenService refreshTokenService,
            OrdersRepository ordersRepository,
            JwtUtils jwtUtils,
            ExtraOrdersRepository extraOrdersRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.ordersRepository = ordersRepository;
        this.extraOrdersRepository = extraOrdersRepository;
    }

    @GetMapping("/adminRoleCheck")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/findUserByTelephone")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findByTelephone(@Valid @RequestParam("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(new UserInformationResponse(user.getOrders(), user.getId(),
                user.getAuto(), user.getFullName(), user.getPhone(), user.getEmail(),
                user.getBonuses(), user.getRoles()));
    }

    @GetMapping("/getUserOrdersByAdmin")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserOrdersByAdmin(@Valid @RequestParam("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(new UserOrdersResponse(user.getOrders(), user));
    }

    @GetMapping("/getUserCarsByAdmin")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserCarsByAdmin(@Valid @RequestParam("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(new UserCarsResponse(user.getAuto(),
                user));
    }


//    @PostMapping("/createOrder")
//    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('ADMIN')")
//    public ResponseEntity<?> newUserOrder(@Valid @RequestBody CreatingOrderRequest creatingOrderRequest) {
//        List<String> strExtraOrders = creatingOrderRequest.getExtraOrders();
//        List<OrdersAdditional> ordersAdditional = new ArrayList<>();
//        //Мэин заказ из стр в енум
//        EOrderMain orderMain;
//        try {
//            orderMain = EOrderMain.valueOf(creatingOrderRequest.getMainOrder());
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Error:Несуществующий основной заказ");
//        }
//
//        //Переводим дополнительные заказы из стр в енум
//        if (strExtraOrders != null && !strExtraOrders.isEmpty()) {
//            Set<EOrderAdditional> extraOrdersList = EnumSet.allOf(EOrderAdditional.class);
//            ordersAdditional = strExtraOrders.stream().map(order -> {
//                Optional<EOrderAdditional> eOrderAdditional = extraOrdersList.stream()
//                        .filter(r -> r.name().equalsIgnoreCase(order))
//                        .findAny();
//                if (eOrderAdditional.isPresent()) {
//                    return extraOrdersRepository.findByName(eOrderAdditional.get())
//                            .orElseThrow(() -> new RuntimeException("Error: Не существующая дополнительная услуга"));
//                } else {
//                    throw new RuntimeException("Error:Не существующая дополнительная услуга");
//                }
//            }).collect(Collectors.toList());
//        }
//
//        var startTime = creatingOrderRequest.getStartTime();
//        var boxNumber = creatingOrderRequest.getBoxNumber();
//        Order newOrder = new Order(orderMain, ordersAdditional, startTime, creatingOrderRequest.getEndTime(),
//                creatingOrderRequest.getAdministrator(), creatingOrderRequest.getSpecialist(), boxNumber,
//                bookingOrderRequest.getBonuses(), true, false,
//                bookingOrderRequest.getComments(), userAuto, user);
//
//        ordersRepository.save(newOrder);
//        return ResponseEntity.ok(new OrderInfoResponse(newOrder.getId(), orderMain, ordersAdditional,
//                newOrder.getStartTime(), newOrder.getEndTime(), newOrder.getAdministrator(), newOrder.getSpecialist(),
//                newOrder.getBoxNumber(), newOrder.getBonuses(), newOrder.isBooked(),
//                newOrder.isExecuted(), newOrder.getComments(), newOrder.getUser().getId()));
//    }
}
