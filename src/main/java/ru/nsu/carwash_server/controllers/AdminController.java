package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Order;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.request.FindingUserInfo;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;

import javax.validation.Valid;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @PostMapping("/adminRoleCheck")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }


    @PostMapping("/findUserByTelephone")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findByTelephone(@Valid @RequestBody FindingUserInfo userInfoRequest) {
        User user = userRepository.findByUsername(userInfoRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/getUserOrders")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserOrders(@Valid @RequestBody FindingUserInfo userInfoRequest) {
        if (!userRepository.existsByUsername(userInfoRequest.getUsername())) {
            throw new RuntimeException("Error: Пользователя с такой почтой не существует");
        }
        Set<Order> userOrders = ordersRepository.getOrdersByUserId(userInfoRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Невозможно получить заказы этого пользователя"));
        return ResponseEntity.ok(userOrders);
    }
}
