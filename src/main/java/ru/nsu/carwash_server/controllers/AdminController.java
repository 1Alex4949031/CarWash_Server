package ru.nsu.carwash_server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.request.FindingUserInfo;
import ru.nsu.carwash_server.payload.response.UserCarsResponse;
import ru.nsu.carwash_server.payload.response.UserInformationResponse;
import ru.nsu.carwash_server.payload.response.UserOrdersResponse;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

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

    @GetMapping("/adminRoleCheck")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/findUserByTelephone")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> findByTelephone(@Valid @RequestBody FindingUserInfo userInfoRequest) {
        User user = userRepository.findByUsername(userInfoRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(new UserInformationResponse(user.getOrders(), user.getId(),
                user.getAuto(), user.getFullName(), user.getPhone(), user.getEmail(),
                user.getBonuses(), user.getRoles()));
    }

    @GetMapping("/getUserOrdersByAdmin")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserOrdersByAdmin(@Valid @RequestBody FindingUserInfo userInfoRequest) {
        User user = userRepository.findByUsername(userInfoRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        Set<String> autoSetString = user.getOrders().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(new UserOrdersResponse(autoSetString, user.getId(), user.getUsername()));
    }

    @GetMapping("/getUserCarsByAdmin")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserCarsByAdmin(@Valid @RequestBody FindingUserInfo userInfoRequest) {
        User user = userRepository.findByUsername(userInfoRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        Set<String> autoSetString = user.getAuto().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(new UserCarsResponse(autoSetString, user.getId(),
                user.getUsername()));
    }
}
