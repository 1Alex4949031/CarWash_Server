package ru.nsu.carwash_server.testContainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



@FixMethodOrder(MethodSorters.NAME_ASCENDING) //задание порядка выполнения тестов по имени
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Sql(scripts = "/sql/insert_roles.sql")
public class RegistrationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @Test
    @DirtiesContext
    public void testRegisterUser() throws JsonProcessingException {
        String baseUrl = "http://localhost:" + port + "/api/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        User user = new User("testuser", "password");
        HttpEntity<User> firstRegisterRequest = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, firstRegisterRequest, String.class);
        //Проверяем что вернул запрос на регистрацию этого юзера
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals(user.getUsername(), savedUser.get().getUsername());
        //Регистрируем нового пользователя с таким же именем
        User secondUser = new User("testuser", "newPassword");
        HttpEntity<User> secondRegisterRequest = new HttpEntity<>(secondUser, headers);
        ResponseEntity<String> secondResponse = restTemplate.postForEntity(baseUrl, secondRegisterRequest, String.class);
        //Проверяем что вернул запрос на регистрацию такого же юзера
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(secondResponse.getBody(), MessageResponse.class);
        String actualValue = messageResponse.getMessage();
        assertEquals("Error: телефон уже занят!", actualValue);
    }

    @Test
    @DirtiesContext
    public void testSecondRegisterUser() {
        String baseUrl = "http://localhost:" + port + "/api/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        User user = new User("testuserTwo", "password");
        HttpEntity<User> firstRegisterRequest = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, firstRegisterRequest, String.class);
        //Проверяем что вернул запрос на регистрацию этого юзера
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals(user.getUsername(), savedUser.get().getUsername());
    }
}