package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.request.NewCarRequest;
import ru.nsu.carwash_server.payload.request.UpdateUserInfoRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.NewCarResponse;
import ru.nsu.carwash_server.payload.response.UserCarsResponse;
import ru.nsu.carwash_server.payload.response.UserOrdersResponse;
import ru.nsu.carwash_server.repository.CarRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.services.RefreshTokenService;
import ru.nsu.carwash_server.security.services.UserDetailsImpl;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;

    private final RefreshTokenService refreshTokenService;

    private final CarRepository carRepository;

    @Autowired
    public UserController(
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            CarRepository carRepository
    ) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.carRepository = carRepository;
    }

    @PutMapping("/updateUserInfo")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        userRepository.changeUserInfo(updateUserInfoRequest.getEmail(), updateUserInfoRequest.getUsername(),
                userId, updateUserInfoRequest.getFullName());
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Пользователь " + userId
                + " получил почту " + updateUserInfoRequest.getEmail()
                + " и новый телефон " + updateUserInfoRequest.getUsername()));
    }

    @PostMapping("/saveNewCar")
    public ResponseEntity<?> saveNewCar(@Valid @RequestBody NewCarRequest newCarRequest) {
        if (carRepository.existsByCarNumber(newCarRequest.getCarNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: машина с таким номером уже есть!"));
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        User user = new User(userId);
        Auto userAuto = new Auto(newCarRequest.getCarNumber(), newCarRequest.getCarClass(), user);
        carRepository.save(userAuto);
        return ResponseEntity.ok(new NewCarResponse(userAuto.getCarNumber(), userAuto.getId(),
                userAuto.getUser().getId(), userAuto.getCarClass()));
    }

    @GetMapping("/getUserOrders")
    public ResponseEntity<?> getUserOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(new UserOrdersResponse(user.getOrders(), user));
    }

    @GetMapping("/getUserCars")
    public ResponseEntity<?> getUserCars() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: Пользователя с таким телефоном не существует"));
        return ResponseEntity.ok(new UserCarsResponse(user.getAuto(), user));
    }
}
