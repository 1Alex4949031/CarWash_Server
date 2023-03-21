package ru.nsu.carwash_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.request.UpdateUserInfoRequest;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.UserRepository;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/updateUserInfo")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        userRepository.changeUserInfo(updateUserInfoRequest.getEmail(), updateUserInfoRequest.getUsername(),
                updateUserInfoRequest.getId());
        return ResponseEntity.ok(new MessageResponse("Пользователь " + updateUserInfoRequest.getId()
                + " получил почту " + updateUserInfoRequest.getEmail()
                + " и новый телефон " + updateUserInfoRequest.getUsername()));
    }
}
