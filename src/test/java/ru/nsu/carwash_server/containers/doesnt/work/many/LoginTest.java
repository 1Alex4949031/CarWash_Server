package ru.nsu.carwash_server.containers.doesnt.work.many;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.carwash_server.payload.request.LoginRequest;
import ru.nsu.carwash_server.payload.request.SignupRequest;
import ru.nsu.carwash_server.payload.response.JwtResponse;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.repository.ExtraOrdersRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.RefreshTokenRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Sql(scripts = "/sql/insert_roles.sql")
public class LoginTest {

    private static final String API_AUTH_SIGNIN = "/api/auth/signin";
    private static final String API_USER_SAVENEWCAR = "/api/user/saveNewCar";
    private static final String API_AUTH_SIGNUP = "/api/auth/signup";
    private static final String API_AUTH_REFRESHTOKEN = "/api/auth/refreshtoken";
    private static final String API_AUTH_SIGNOUT = "/api/auth/signout";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ExtraOrdersRepository extraOrdersRepository;

    @Test
    @Order(2)
    public void loginUserTest() throws JsonProcessingException {
        HttpHeaders signUpHeaders = new HttpHeaders();
        signUpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пльзователя
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser")
                .role(null)
                .password("password")
                .build();
        HttpEntity<SignupRequest> registerRequest = new HttpEntity<>(signupRequest, signUpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(API_AUTH_SIGNUP, registerRequest, String.class);

        //Проверяем что вернул запрос на регистрацию этого юзера
        assertEquals(HttpStatus.OK, response.getStatusCode());

        HttpHeaders signInHeaders = new HttpHeaders();
        signInHeaders.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        LoginRequest firstLoginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password")
                .build();
        HttpEntity<LoginRequest> firstSignInRequest = new HttpEntity<>(firstLoginRequest, signInHeaders);
        ResponseEntity<String> firstLoginResponse = restTemplate.postForEntity(API_AUTH_SIGNIN, firstSignInRequest, String.class);
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
        HttpEntity<LoginRequest> secondSignInRequest = new HttpEntity<>(secondLoginRequest, signInHeaders);
        ResponseEntity<String> secondLoginResponse = restTemplate.postForEntity(API_AUTH_SIGNIN, secondSignInRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, secondLoginResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(secondLoginResponse.getBody(), MessageResponse.class);
        assertEquals("Error: такого пользователя не существует!", messageResponse.getMessage());
    }
}
