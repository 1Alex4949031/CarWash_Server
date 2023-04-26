package ru.nsu.carwash_server.containers.one;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.carwash_server.models.RefreshToken;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.ERole;
import ru.nsu.carwash_server.payload.request.LoginRequest;
import ru.nsu.carwash_server.payload.request.NewCarRequest;
import ru.nsu.carwash_server.payload.request.SignupRequest;
import ru.nsu.carwash_server.payload.request.TokenRefreshRequest;
import ru.nsu.carwash_server.payload.response.JwtResponse;
import ru.nsu.carwash_server.payload.response.MessageResponse;
import ru.nsu.carwash_server.payload.response.NewCarResponse;
import ru.nsu.carwash_server.payload.response.TokenRefreshResponse;
import ru.nsu.carwash_server.repository.CarRepository;
import ru.nsu.carwash_server.repository.ExtraOrdersRepository;
import ru.nsu.carwash_server.repository.OrdersRepository;
import ru.nsu.carwash_server.repository.RefreshTokenRepository;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Sql(scripts = "/sql/insert_roles.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthTests {

    private static String bearerToken;
    private static String refreshToken;
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
    private CarRepository carRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ExtraOrdersRepository extraOrdersRepository;

    @Test
    @Transactional
    public void testEBookOrder() {
        // получаем что пользователя из базы данных не сбежал
        User savedUser = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new RuntimeException("Юзер почему-то пропал из бд...."));

        //Проверяем то что прошлый тест реально сработал
        assertTrue(carRepository.existsByCarNumber("Y363TT"));
        assertFalse(refreshTokenRepository.findByUser(savedUser).isPresent());

    }

    @Test
    @Transactional
    public void testDRefreshToken() throws JsonProcessingException, InterruptedException {
        // получаем что пользователя из базы данных не сбежал
        User savedUser = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new RuntimeException("Юзер почему-то пропал из бд...."));

        //Проверяем то что прошлый тест реально сработал
        assertTrue(carRepository.existsByCarNumber("Y363TT"));


        RefreshToken startRefreshToken = refreshTokenRepository.findByUser(savedUser)
                .orElseThrow(() -> new RuntimeException("И где токен...."));

        HttpHeaders refreshTokenHeaders = new HttpHeaders();
        refreshTokenHeaders.setContentType(MediaType.APPLICATION_JSON);

        //Зарефрешим текущий токен
        TokenRefreshRequest tokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(startRefreshToken.getToken())
                .build();
        HttpEntity<TokenRefreshRequest> tokenRefreshHTTPRequest = new HttpEntity<>(tokenRefreshRequest, refreshTokenHeaders);
        ResponseEntity<String> tokenRefreshHTTPResponse = restTemplate
                .postForEntity(API_AUTH_REFRESHTOKEN, tokenRefreshHTTPRequest, String.class);
        assertEquals(HttpStatus.OK, tokenRefreshHTTPResponse.getStatusCode());
        String responseBody = tokenRefreshHTTPResponse.getBody();
        TokenRefreshResponse tokenRefreshResponse = new ObjectMapper().readValue(responseBody, TokenRefreshResponse.class);
        bearerToken = tokenRefreshResponse.getAccessToken();
        refreshToken = tokenRefreshRequest.getRefreshToken();

        //Отравим какой-то запрос и проверим что время действия токена реально прошло
        HttpHeaders newCarHeaders = new HttpHeaders();
        newCarHeaders.setContentType(MediaType.APPLICATION_JSON);
        newCarHeaders.set("Authorization", "Bearer " + bearerToken);
        assertNotNull(bearerToken);

        //Пытаем добавить новое авто с плохим токеном
        Thread.sleep(4100);
        NewCarRequest firstNewCarRequest = NewCarRequest.builder()
                .carNumber("newCar")
                .carClass(3)
                .build();
        HttpEntity<NewCarRequest> newCarHTTPRequest = new HttpEntity<>(firstNewCarRequest, newCarHeaders);
        ResponseEntity<String> newCarResponse = restTemplate
                .postForEntity(API_USER_SAVENEWCAR, newCarHTTPRequest, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, newCarResponse.getStatusCode());

        //Теперь сделаем опять хороший токен
        //Зарефрешим текущий токен
        TokenRefreshRequest newTokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();
        HttpEntity<TokenRefreshRequest> newTokenRefreshHTTPRequest = new HttpEntity<>(newTokenRefreshRequest, refreshTokenHeaders);
        ResponseEntity<String> newTokenRefreshHTTPResponse = restTemplate
                .postForEntity(API_AUTH_REFRESHTOKEN, newTokenRefreshHTTPRequest, String.class);
        assertEquals(HttpStatus.OK, newTokenRefreshHTTPResponse.getStatusCode());
        String newResponseBody = newTokenRefreshHTTPResponse.getBody();
        TokenRefreshResponse newTokenRefreshResponse = new ObjectMapper().readValue(newResponseBody, TokenRefreshResponse.class);
        bearerToken = newTokenRefreshResponse.getAccessToken();
        refreshToken = newTokenRefreshResponse.getRefreshToken();
        assertTrue(refreshTokenRepository.findByUser(savedUser).isPresent());

        //Сделаем логаут и удалим новый рефрер токен
        HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.setContentType(MediaType.APPLICATION_JSON);
        logoutHeaders.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> logoutHTTPRequest = new HttpEntity<>(null, logoutHeaders);
        ResponseEntity<String> logoutResponse = restTemplate
                .postForEntity(API_AUTH_SIGNOUT, logoutHTTPRequest, String.class);
        //Сам запрос выполнился хорошо, а из репозитория рефреш токена удалился токен
        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
        assertFalse(refreshTokenRepository.findByUser(savedUser).isPresent());
    }

    /**
     * Тут мы нашему любиму пользователю добавляем машину.
     * Затем проверяем что нельзя добавить еще одну машину с таким же номером.
     * Затем добавляем этому же пользователю вторую машину, смотрим что айди меняет и инфа.
     *
     * @throws JsonProcessingException если с джейсоном что-то пойдёт не так
     */
    @Test
    @Transactional
    public void testCAddCr() throws JsonProcessingException, InterruptedException {
        // получаем что пользователя из базы данных не сбежал
        User savedUser = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new RuntimeException("Юзер почему-то пропал из бд...."));
        assertTrue(refreshTokenRepository.findByUser(savedUser).isPresent());
        assertNotNull(bearerToken);
        //assertTrue(roleRepository.findByName(ERole.ROLE_ADMIN).isPresent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);

        //Добавляем первое авто
        NewCarRequest firstNewCarRequest = NewCarRequest.builder()
                .carNumber("Y363TT")
                .carClass(1)
                .build();
        HttpEntity<NewCarRequest> firstNewCarHTTPRequest = new HttpEntity<>(firstNewCarRequest, headers);
        ResponseEntity<String> firstNewCarResponse = restTemplate
                .postForEntity(API_USER_SAVENEWCAR, firstNewCarHTTPRequest, String.class);
        assertEquals(HttpStatus.OK, firstNewCarResponse.getStatusCode());

        //Проверяем джейсон из запроса
        String responseBody = firstNewCarResponse.getBody();
        NewCarResponse firstNewCarJSONResponse = new ObjectMapper().readValue(responseBody, NewCarResponse.class);
        assertNotNull(bearerToken);
        assertEquals(1L, firstNewCarJSONResponse.getCarId());
        assertEquals("Y363TT", firstNewCarJSONResponse.getCarNumber());
        assertEquals(1, firstNewCarJSONResponse.getCarClass());
        assertTrue(carRepository.findByUser(savedUser).isPresent());
        assertTrue(carRepository.existsByCarNumber("Y363TT"));


        //Добавляем авто с таким же номером
        NewCarRequest sameNewCarRequest = NewCarRequest.builder()
                .carNumber("Y363TT")
                .carClass(1)
                .build();
        HttpEntity<NewCarRequest> sameNewCarHTTPRequest = new HttpEntity<>(sameNewCarRequest, headers);
        ResponseEntity<String> sameNewCarResponse = restTemplate
                .postForEntity(API_USER_SAVENEWCAR, sameNewCarHTTPRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, sameNewCarResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(sameNewCarResponse.getBody(), MessageResponse.class);
        assertEquals("Error: машина с таким номером уже есть!", messageResponse.getMessage());


        //Добавим вторую машину нашего юзеру
        NewCarRequest secondNewCarRequest = NewCarRequest.builder()
                .carNumber("S555LL")
                .carClass(2)
                .build();
        HttpEntity<NewCarRequest> secondNewCarHTTPRequest = new HttpEntity<>(secondNewCarRequest, headers);
        ResponseEntity<String> secondNewCarResponse = restTemplate
                .postForEntity(API_USER_SAVENEWCAR, secondNewCarHTTPRequest, String.class);
        assertEquals(HttpStatus.OK, secondNewCarResponse.getStatusCode());
        assertTrue(carRepository.existsByCarNumber("S555LL"));


        //Проверяем джейсон из второго запроса
        String secondResponseBody = secondNewCarResponse.getBody();
        NewCarResponse secondNewCarJSONResponse = new ObjectMapper().readValue(secondResponseBody, NewCarResponse.class);
        assertEquals(2L, secondNewCarJSONResponse.getCarId());
        assertEquals("S555LL", secondNewCarJSONResponse.getCarNumber());
        assertEquals(2, secondNewCarJSONResponse.getCarClass());
    }

    /**
     * Логинимся в аккаунт, получаем jwt токен для дальнейших операций.
     * Затем пытаемся залогиниться тем пользователем которого нет в бд, проверяем ошибки.
     *
     * @throws JsonProcessingException если с джейсоном что-то пойдёт не так
     */
    @Test
    @Transactional
    public void testBLogin() throws JsonProcessingException {
        //assertTrue(roleRepository.findByName(ERole.ROLE_USER).isPresent());
        // получаем что пользователя из базы данных не сбежал
        User savedUser = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new RuntimeException("Юзер почему-то пропал из бд...."));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        LoginRequest firstLoginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password")
                .build();
        HttpEntity<LoginRequest> firstSignInRequest = new HttpEntity<>(firstLoginRequest, headers);
        ResponseEntity<String> firstLoginResponse = restTemplate.postForEntity(API_AUTH_SIGNIN, firstSignInRequest, String.class);
        assertEquals(HttpStatus.OK, firstLoginResponse.getStatusCode());
        String responseBody = firstLoginResponse.getBody();

        //десериализация строки JSON в объект LoginResponse, используя библиотеку Jackson
        JwtResponse loginResponse = new ObjectMapper().readValue(responseBody, JwtResponse.class);
        bearerToken = loginResponse.getToken();
        refreshToken = loginResponse.getRefreshToken();
        assertNotNull(bearerToken);
        assertEquals("Bearer", loginResponse.getType());
        assertEquals("testuser", loginResponse.getUsername());
        assertTrue(loginResponse.getRoles().contains("ROLE_USER"));
        assertEquals(1L, loginResponse.getId());
        assertTrue(userRepository.existsByUsername("testuser"));
        assertTrue(refreshTokenRepository.findByUser(savedUser).isPresent());


        //Проверяем что никакую херню не вернули
        assertFalse(loginResponse.getRoles().contains("ROLE_ADMIN"));

        //Попробуем залогиниться человеком, которого нет в бд

        LoginRequest secondLoginRequest = LoginRequest.builder()
                .username("testUser")
                .password("password")
                .build();
        HttpEntity<LoginRequest> secondSignInRequest = new HttpEntity<>(secondLoginRequest, headers);
        ResponseEntity<String> secondLoginResponse = restTemplate.postForEntity(API_AUTH_SIGNIN, secondSignInRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, secondLoginResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(secondLoginResponse.getBody(), MessageResponse.class);
        assertEquals("Error: такого пользователя не существует!", messageResponse.getMessage());
    }

    @Test
    public void testARegister() throws JsonProcessingException {
        assertTrue(roleRepository.findByName(ERole.ROLE_USER).isPresent());
        assertTrue(roleRepository.findByName(ERole.ROLE_ADMIN).isPresent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Создаём нового первого пользователя
        SignupRequest firstSignupRequest = SignupRequest.builder()
                .username("testuser")
                .role(null)
                .password("password")
                .build();
        HttpEntity<SignupRequest> firstRegisterRequest = new HttpEntity<>(firstSignupRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_AUTH_SIGNUP, firstRegisterRequest, String.class);

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
        ResponseEntity<String> secondResponse = restTemplate.postForEntity(API_AUTH_SIGNUP, secondRegisterRequest, String.class);
        //Проверяем что вернул запрос на регистрацию такого же юзера
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode());
        //Проверяем какой messageResponse, то есть json ошибки вернул запрос
        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(secondResponse.getBody(), MessageResponse.class);
        assertEquals("Error: телефон уже занят!", messageResponse.getMessage());
    }
}
