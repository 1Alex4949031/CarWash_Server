package ru.nsu.carwash_server.containers.one;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.DependsOn;
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
import ru.nsu.carwash_server.payload.request.LoginRequest;
import ru.nsu.carwash_server.payload.request.SignupRequest;
import ru.nsu.carwash_server.payload.response.JwtResponse;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Sql(scripts = "/sql/insert_roles.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthTests {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DependsOn("testLogin")
    public void testNewCar() {
        // получаем зарегистрированного пользователя из базы данных
        Optional<User> savedUser = userRepository.findByUsername("testuser");
        assertTrue(savedUser.isPresent());
    }

    @Test
    @DependsOn("testSignUpUser")
    public void testLogin() throws JsonProcessingException {
        // получаем зарегистрированного пользователя из базы данных
        Optional<User> savedUser = userRepository.findByUsername("testuser");
        assertTrue(savedUser.isPresent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        LoginRequest firstLoginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password")
                .build();
        HttpEntity<LoginRequest> firstSignInRequest = new HttpEntity<>(firstLoginRequest, headers);
        ResponseEntity<String> firstLoginResponse = restTemplate.postForEntity("/api/auth/signin", firstSignInRequest, String.class);
        assertEquals(HttpStatus.OK, firstLoginResponse.getStatusCode());
        String responseBody = firstLoginResponse.getBody();

        //десериализация строки JSON в объект LoginResponse, используя библиотеку Jackson
        JwtResponse loginResponse = new ObjectMapper().readValue(responseBody, JwtResponse.class);
        String bearerToken = loginResponse.getToken();
        assertNotNull(bearerToken);
        assertEquals("Bearer", loginResponse.getType());
        assertEquals("testuser", loginResponse.getUsername());
        assertTrue(loginResponse.getRoles().contains("ROLE_USER"));
        assertEquals(1L, loginResponse.getId());

        //Проверяем что никакую херню не вернули
        assertFalse(loginResponse.getRoles().contains("ROLE_ADMIN"));

        //Попробуем залогиниться человеком, которого нет в бд

        LoginRequest secondLoginRequest = LoginRequest.builder()
                .username("testUser")
                .password("password")
                .build();
        HttpEntity<LoginRequest> secondSignInRequest = new HttpEntity<>(secondLoginRequest, headers);
        ResponseEntity<String> secondLoginResponse = restTemplate.postForEntity("/api/auth/signin", secondSignInRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, secondLoginResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(secondLoginResponse.getBody(), MessageResponse.class);
        assertEquals("Error: такого пользователя не существует!", messageResponse.getMessage());
    }

    @Test
    public void testSignUpUser() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        SignupRequest firstSignupRequest = SignupRequest.builder()
                .username("testuser")
                .role(null)
                .password("password")
                .build();
        HttpEntity<SignupRequest> firstRegisterRequest = new HttpEntity<>(firstSignupRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/signup", firstRegisterRequest, String.class);

        //Проверяем что вернул запрос на регистрацию этого юзера
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<User> savedUser = userRepository.findByUsername(firstSignupRequest.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals(firstSignupRequest.getUsername(), savedUser.get().getUsername());

        //Регистрируем нового пользователя с таким же именем
        SignupRequest secondSignupRequest = SignupRequest.builder()
                .username("testuser")
                .role(null)
                .password("newPassword")
                .build();
        HttpEntity<SignupRequest> secondRegisterRequest = new HttpEntity<>(secondSignupRequest, headers);
        ResponseEntity<String> secondResponse = restTemplate.postForEntity("/api/auth/signup", secondRegisterRequest, String.class);
        //Проверяем что вернул запрос на регистрацию такого же юзера
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(secondResponse.getBody(), MessageResponse.class);
        assertEquals("Error: телефон уже занят!", messageResponse.getMessage());
    }
}
