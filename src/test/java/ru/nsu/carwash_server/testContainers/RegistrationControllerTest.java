package ru.nsu.carwash_server.testContainers;

import org.junit.Before;
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
import ru.nsu.carwash_server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }
    @Test
    @DirtiesContext
    public void testRegisterUser() {
        String baseUrl = "http://localhost:" + port + "/api/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        User user = new User("testUser", "password");
        HttpEntity<User> registerFirstRequest = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, registerFirstRequest, String.class);
        //Проверяем то что запрос норм и юзер реально зарегистрировался, существует в бд
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals(user.getUsername(), savedUser.get().getUsername());
        //Проверяем то что никакой фейковый юзер не появился
        Optional<User> notSavedUser = userRepository.findByUsername("fakeUser");
        assertFalse(notSavedUser.isPresent());
        //пытаемся второй раз зарегистрироваться с существующим никнеймом
        User userWithSameName = new User("testUser", "samePassword");
        HttpEntity<User> registerSecondRequest = new HttpEntity<>(userWithSameName, headers);
        ResponseEntity<String> registerSecondResponse = restTemplate.postForEntity(baseUrl, registerSecondRequest, String.class);
        //Проверяем то что не может второй раз зарегаться такой же юзер
        assertEquals(HttpStatus.BAD_REQUEST, registerSecondResponse.getStatusCode());
    }


    /*@Test
    @DirtiesContext
    public void testRegisterUserTwo() {
        String baseUrl = "http://localhost:" + port + "/api/auth/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        User user = new User("testuser", "password");
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);
        System.out.println(response.getStatusCodeValue());
        System.out.println(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals(user.getUsername(), savedUser.get().getUsername());
    }*/
}