package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.exception.NotInDataBaseException;
import ru.nsu.carwash_server.models.helpers.OrdersPriceAndTimeInfo;
import ru.nsu.carwash_server.payload.request.UpdateUserInfoRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.UserOrdersResponse;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.services.RefreshTokenService;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class
UserController {
    private final UserRepository userRepository;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserController(
            UserRepository userRepository,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PutMapping("/updateUserInfo")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotInDataBaseException("пользователей не найден пользователь с айди: ", userId.toString()));
        userRepository.changeUserInfo(updateUserInfoRequest.getEmail(), updateUserInfoRequest.getUsername(),
                userId, updateUserInfoRequest.getFullName());
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Пользователь " + userId
                + " получил почту " + user.getEmail()
                + " и новый телефон " + user.getUsername()));
    }

    @GetMapping("/getUserOrders")
    public ResponseEntity<?> getUserOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotInDataBaseException("пользователей не найден пользователь с айди: ", userId.toString()));
        List<OrdersPriceAndTimeInfo> ordersPriceAndTimeInfo = new ArrayList<>();
        for (var item: user.getOrders()){
            ordersPriceAndTimeInfo.add(new OrdersPriceAndTimeInfo(item.getOrderType(), item.getPrice(),item.getStartTime()));
        }
        return ResponseEntity.ok(new UserOrdersResponse(ordersPriceAndTimeInfo));
    }
}
