package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.DocumentDefaultsDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.Auto;
import ru.nsu.carwash_server.payload.request.NewCarRequest;
import ru.nsu.carwash_server.payload.request.UpdateUserInfoRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.CarRepository;
import ru.nsu.carwash_server.repository.UserRepository;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CarRepository carRepository;

    @PostMapping("/updateUserInfo")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        userRepository.changeUserInfo(updateUserInfoRequest.getEmail(), updateUserInfoRequest.getUsername(),
                updateUserInfoRequest.getId(), updateUserInfoRequest.getFullName());
        return ResponseEntity.ok(new MessageResponse("Пользователь " + updateUserInfoRequest.getId()
                + " получил почту " + updateUserInfoRequest.getEmail()
                + " и новый телефон " + updateUserInfoRequest.getUsername()));
    }

    @PostMapping("/saveNewCar")
    public ResponseEntity<?> saveNewCar(@Valid @RequestBody NewCarRequest newCarRequest) {
        Auto userAuto = new Auto();
        userAuto.setUsers(newCarRequest.getUser());
        userAuto.setCarNumber(newCarRequest.getCarNumber());
        userAuto.setCarClass(newCarRequest.getCarClass());
        carRepository.save(userAuto);
        return ResponseEntity.ok(new MessageResponse("Пользователь " + newCarRequest.getUser().getId()
                + " добавил машину " + newCarRequest.getCarNumber()));
    }
}
