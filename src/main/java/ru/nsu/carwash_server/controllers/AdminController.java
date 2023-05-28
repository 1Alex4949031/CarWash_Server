package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Role;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.ERole;
import ru.nsu.carwash_server.models.exception.NotInDataBaseException;
import ru.nsu.carwash_server.models.exception.UserNotFoundException;
import ru.nsu.carwash_server.models.helpers.OrdersPriceAndTimeInfo;
import ru.nsu.carwash_server.payload.request.UpdatePolishingOrderRequest;
import ru.nsu.carwash_server.payload.request.UpdateTireOrderRequest;
import ru.nsu.carwash_server.payload.request.UpdateUserInfoRequest;
import ru.nsu.carwash_server.payload.request.UpdateWashingOrderRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.UserInformationResponse;
import ru.nsu.carwash_server.payload.response.UserOrdersResponse;
import ru.nsu.carwash_server.repository.OrdersPolishingRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.OrdersTireRepository;
import ru.nsu.carwash_server.repository.OrdersWashingRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.jwt.JwtUtils;
import ru.nsu.carwash_server.security.services.RefreshTokenService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final OrdersWashingRepository ordersWashingRepository;

    private final OrdersPolishingRepository polishingRepository;

    private final OrdersTireRepository tireRepository;


    @Autowired
    public AdminController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenService refreshTokenService,
            OrdersRepository ordersRepository,
            JwtUtils jwtUtils,
            OrdersWashingRepository ordersWashingRepository,
            OrdersPolishingRepository polishingRepository,
            OrdersTireRepository tireRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.polishingRepository = polishingRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.ordersRepository = ordersRepository;
        this.ordersWashingRepository = ordersWashingRepository;
        this.tireRepository = tireRepository;
    }

    @GetMapping("/adminRoleCheck")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess() {
        return ResponseEntity.ok(new MessageResponse("Доступ есть"));
    }

    @GetMapping("/findUserByTelephone")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findByTelephone(@Valid @RequestParam("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotInDataBaseException("пользователей не найден пользователь: ", username));
        return ResponseEntity.ok(new UserInformationResponse(user.getOrders(), user.getId(),
                user.getFullName(), user.getPhone(), user.getEmail(),
                user.getBonuses(), user.getRoles()));
    }

    @GetMapping("/getUserOrdersByAdmin")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserOrdersByAdmin(@Valid @RequestParam("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotInDataBaseException("пользователей не найден пользователь: ", username));
        List<OrdersPriceAndTimeInfo> ordersPriceAndTimeInfo = new ArrayList<>();
        for (var item : user.getOrders()) {
            ordersPriceAndTimeInfo.add(new OrdersPriceAndTimeInfo(item.getOrderType(), item.getPrice(), item.getStartTime()));
        }
        return ResponseEntity.ok(new UserOrdersResponse(ordersPriceAndTimeInfo));
    }

    @PutMapping("/updateWashingOrder")
    public ResponseEntity<?> updateWashingOrder(@Valid @RequestBody UpdateWashingOrderRequest updateWashingOrderRequest) {
        ordersWashingRepository.updateWashingOrderInfo(updateWashingOrderRequest.getName(), updateWashingOrderRequest.getPriceFirstType(),
                updateWashingOrderRequest.getPriceSecondType(), updateWashingOrderRequest.getPriceThirdType(),
                updateWashingOrderRequest.getTimeFirstType(), updateWashingOrderRequest.getTimeSecondType(),
                updateWashingOrderRequest.getTimeThirdType(), updateWashingOrderRequest.getRole());
        return ResponseEntity.ok(new MessageResponse("Услуга "+ updateWashingOrderRequest.getName() +" изменена"));
    }

    @PutMapping("/updatePolishingOrder")
    public ResponseEntity<?> updatePolishingOrder(@Valid @RequestBody UpdatePolishingOrderRequest updatePolishingOrderRequest) {
        polishingRepository.updatePolishingOrder(updatePolishingOrderRequest.getName(), updatePolishingOrderRequest.getPriceFirstType(),
                updatePolishingOrderRequest.getPriceSecondType(), updatePolishingOrderRequest.getPriceThirdType(),
                updatePolishingOrderRequest.getTimeFirstType(), updatePolishingOrderRequest.getTimeSecondType(),
                updatePolishingOrderRequest.getTimeThirdType());
        return ResponseEntity.ok(new MessageResponse("Услуга "+ updatePolishingOrderRequest.getName() +" изменена"));
    }

    @PutMapping("/updateTireOrder")
    public ResponseEntity<?> updateTireOrder(@Valid @RequestBody UpdateTireOrderRequest updateTireOrderRequest) {
        System.out.println(updateTireOrderRequest);
        tireRepository.updateTireOrderInfo(updateTireOrderRequest.getName(), updateTireOrderRequest.getPrice_r_13(),
                updateTireOrderRequest.getPrice_r_14(), updateTireOrderRequest.getPrice_r_15(),
                updateTireOrderRequest.getPrice_r_16(), updateTireOrderRequest.getPrice_r_17(),
                updateTireOrderRequest.getPrice_r_18(), updateTireOrderRequest.getPrice_r_19(), updateTireOrderRequest.getPrice_r_20(),
                updateTireOrderRequest.getPrice_r_21(), updateTireOrderRequest.getPrice_r_22(), updateTireOrderRequest.getRole());
        return ResponseEntity.ok(new MessageResponse("Услуга "+ updateTireOrderRequest.getName() +" изменена"));
    }

    @PutMapping("/updateUserInfo")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        User user = userRepository.findByUsername(updateUserInfoRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(updateUserInfoRequest.getUsername()));
        Long userId = user.getId();
        Set<Role> roles;
        Set<String> strRoles = updateUserInfoRequest.getRoles();

        Set<ERole> rolesList = EnumSet.allOf(ERole.class);
        roles = strRoles.stream().map(role -> {
            Optional<ERole> enumRole = rolesList.stream()
                    .filter(r -> r.name().equalsIgnoreCase(role))
                    .findAny();
            if (enumRole.isPresent()) {
                return roleRepository.findByName(enumRole.get())
                        .orElseThrow(() -> new NotInDataBaseException("ролей не найдена роль: ", enumRole.get().name()));
            } else {
                throw new RuntimeException("Error: Invalid role:" + strRoles);
            }
        }).collect(Collectors.toSet());
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new NotInDataBaseException("ролей не найдена роль: ", ERole.ROLE_USER.name()));
        roles.add(userRole);


        user.setRoles(roles);
        userRepository.changeUserInfo(updateUserInfoRequest.getEmail(), updateUserInfoRequest.getUsername(),
                userId, updateUserInfoRequest.getFullName());
        refreshTokenService.deleteAllByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Пользователь " + userId
                + " получил почту " + updateUserInfoRequest.getEmail()
                + " и новый телефон " + updateUserInfoRequest.getUsername()));
    }
}
