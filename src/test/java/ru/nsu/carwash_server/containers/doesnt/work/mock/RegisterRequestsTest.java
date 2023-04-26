package ru.nsu.carwash_server.containers.doesnt.work.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.carwash_server.TestHelper;
import ru.nsu.carwash_server.models.RefreshToken;
import ru.nsu.carwash_server.models.Role;
import ru.nsu.carwash_server.models.User;
import ru.nsu.carwash_server.models.constants.ERole;
import ru.nsu.carwash_server.payload.request.LoginRequest;
import ru.nsu.carwash_server.payload.request.SignupRequest;
import ru.nsu.carwash_server.repository.RoleRepository;
import ru.nsu.carwash_server.repository.UserRepository;
import ru.nsu.carwash_server.security.jwt.JwtUtils;
import ru.nsu.carwash_server.security.services.RefreshTokenService;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegisterRequestsTest {

    private static final String API_AUTH_SIGNIN = "/api/auth/signin";
    private static final String API_AUTH_SIGNUP = "/api/auth/signup";
    private static final String API_AUTH_REFRESHTOKEN = "/api/auth/refreshtoken";

    private static final String API_AUTH_SIGNOUT = "/api/auth/signout";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void repeat() {

    }

    /**
     * В общем, в этом тесте мы используем заглушки для методов с репозиторием.
     * В запросе для регистрации мы используем имя и пароль, затем проверяем их значение.
     * Еще проверяем что роль пользователя User, потому что
     * если никакая роль не передается, то он по автомату обычный User.
     *
     * @throws Exception какая-то ошибка
     */
    @Test
    public void registrationTest() throws Exception {
        String username = "test_user";
        String password = "test_password";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setPassword(password);

        // Настройка заглушки для PasswordEncoder
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // Настройка заглушки для UserRepository
        User savedUser = new User();
        savedUser.setUsername(signupRequest.getUsername());
        savedUser.setPassword(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // Настройка заглушки для RoleRepository
        Role userRole = new Role(ERole.ROLE_USER);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        // Выполнение тестового запроса через mockMvc
        mockMvc.perform(post(API_AUTH_SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        // Проверка, что объект User был успешно сохранен с указанным значением password и ролью
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(capturedUser.getRoles().size(), 1);
        assertEquals(capturedUser.getRoles().iterator().next().getName(), ERole.ROLE_USER);
        assertEquals(capturedUser.getPhone(), signupRequest.getUsername());
        assertTrue(passwordEncoder.matches(signupRequest.getPassword(), capturedUser.getPassword()));
    }

    @Test
    public void testLogin() throws Exception {
        String username = "test_user";
        String password = "test_password";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setPassword(password);

        // Настройка заглушки для PasswordEncoder
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // Настройка заглушки для UserRepository
        User savedUser = new User();
        savedUser.setUsername(signupRequest.getUsername());
        savedUser.setPassword(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // Настройка заглушки для RoleRepository
        Role userRole = new Role(ERole.ROLE_USER);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("test_token");
        Mockito.when(refreshTokenService.createRefreshToken(Mockito.anyLong())).thenReturn(refreshToken);

        // Выполнение тестового запроса через mockMvc
        mockMvc.perform(post(API_AUTH_SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        // Выполняем вход в систему с использованием имени пользователя и пароля
        mockMvc.perform(post(API_AUTH_SIGNIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}