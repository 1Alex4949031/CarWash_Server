package ru.nsu.carwash_server.controllers;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FlywayTest
public class RegistrationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterUser() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем тестового пользователя
        User user = new User("testuser", "password");

        // Отправляем POST запрос на регистрацию
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        // Проверяем, что запрос вернул статус 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем, что пользователь был сохранен в базе данных
        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals(user.getUsername(), savedUser.get().getUsername());
    }
}